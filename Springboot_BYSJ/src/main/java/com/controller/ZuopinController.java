










package com.controller;

import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaibaomingEntity;
import com.entity.JingsaiJiaofeiJiluEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaibaomingService;
import com.service.JingsaiJiaofeiJiluService;
import com.service.JingsaixinxiService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 学生作品管理控制器
 * 功能：学生提交/更新参赛作品、教师查看和下载作品
 */
@RestController
@RequestMapping("/zuopin")
@Slf4j
public class ZuopinController {

    @Autowired
    private JingsaibaomingService jingsaibaomingService;
    
    @Autowired
    private JingsaiJiaofeiJiluService jingsaiJiaofeiJiluService;
    
    @Autowired
    private JingsaixinxiService jingsaixinxiService;

    @Autowired
    private com.service.XiaoxiTongzhiService xiaoxiTongzhiService;

    // 【论文3.1.1(4)】版本控制：保留最近3个版本
    private static final int MAX_VERSIONS = 3;
    
    // 【论文5.2.3】分片上传：分片大小 5MB
    private static final int CHUNK_SIZE = 5 * 1024 * 1024;
    
    // 分片上传状态跟踪：fileIdentifier -> 已上传分片索引集合
    private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> chunkUploadProgress = new ConcurrentHashMap<>();
    
    // 分片上传元数据：fileIdentifier -> 文件信息
    private static final ConcurrentHashMap<String, Map<String, Object>> chunkUploadMeta = new ConcurrentHashMap<>();

    /**
     * 【论文3.1.1(4)】清理旧版本作品文件
     * 保留最近3个版本，超出部分删除
     * 
     * @param xuehao 学号
     * @param baomingId 报名ID
     * @param projectRoot 项目根目录
     */
    private void cleanupOldVersions(String xuehao, Long baomingId, String projectRoot) {
        String zuopinDir = projectRoot + "/upload/zuopin/";
        File dir = new File(zuopinDir);
        if (!dir.exists()) return;

        // 查找该学号+报名ID的所有版本文件（文件名格式：xuehao_baomingId_timestamp.ext）
        String prefix = xuehao + "_" + baomingId + "_";
        List<File> versionFiles = new ArrayList<>();
        for (File f : dir.listFiles()) {
            if (f.getName().startsWith(prefix)) {
                versionFiles.add(f);
            }
        }

        // 按创建时间降序排序（最新版本在前）
        versionFiles.sort((a, b) -> {
            try {
                BasicFileAttributes attrA = Files.readAttributes(a.toPath(), BasicFileAttributes.class);
                BasicFileAttributes attrB = Files.readAttributes(b.toPath(), BasicFileAttributes.class);
                return attrB.creationTime().compareTo(attrA.creationTime());
            } catch (Exception e) {
                return Long.compare(b.lastModified(), a.lastModified());
            }
        });

        // 保留最近3个版本，删除更早的版本
        if (versionFiles.size() > MAX_VERSIONS) {
            for (int i = MAX_VERSIONS; i < versionFiles.size(); i++) {
                File oldVersion = versionFiles.get(i);
                try {
                    oldVersion.delete();
                    log.info("【版本控制】删除旧版本文件：{}（保留最近{}个版本）", oldVersion.getName(), MAX_VERSIONS);
                } catch (Exception e) {
                    log.warn("【版本控制】删除旧版本失败：{}", oldVersion.getName());
                }
            }
        }
        log.info("【版本控制】学号{} 报名{} 当前保留{}个版本", xuehao, baomingId, Math.min(versionFiles.size(), MAX_VERSIONS));
    }

    /**
     * 【论文3.1.1(4)】查询作品版本历史
     * 返回该报名记录的所有版本文件列表（最多3个）
     *
     * @param baomingId 报名ID
     * @param request HTTP请求
     * @return 版本历史列表
     */
    @GetMapping("/versions/{baomingId}")
    public R getVersionHistory(@PathVariable("baomingId") Long baomingId, HttpServletRequest request) {
        try {
            JingsaibaomingEntity baoming = jingsaibaomingService.selectById(baomingId);
            if (baoming == null) {
                return R.error("报名信息不存在");
            }

            String projectRoot = System.getProperty("user.dir");
            String zuopinDir = projectRoot + "/upload/zuopin/";
            File dir = new File(zuopinDir);
            if (!dir.exists()) {
                return R.ok().put("data", new ArrayList<>());
            }

            // 查找该报名的所有版本文件
            String prefix = baoming.getXuehao() + "_" + baomingId + "_";
            List<File> versionFiles = new ArrayList<>();
            for (File f : dir.listFiles()) {
                if (f.getName().startsWith(prefix)) {
                    versionFiles.add(f);
                }
            }

            // 按修改时间降序排序
            versionFiles.sort((a, b) -> Long.compare(b.lastModified(), a.lastModified()));

            List<Map<String, Object>> versions = new ArrayList<>();
            int versionNum = versionFiles.size();
            for (File f : versionFiles) {
                Map<String, Object> v = new HashMap<>();
                v.put("version", versionNum--);
                v.put("fileName", f.getName());
                v.put("filePath", "upload/zuopin/" + f.getName());
                v.put("fileSize", f.length());
                v.put("uploadTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(f.lastModified()));
                v.put("isCurrent", f.getName().equals(new File(projectRoot, baoming.getCansaizuopin()).getName()));
                versions.add(v);
            }

            log.info("查询版本历史 - 报名ID: {}，版本数: {}", baomingId, versions.size());
            return R.ok().put("data", versions);
        } catch (Exception e) {
            log.error("查询版本历史异常", e);
            return R.error("查询版本历史失败");
        }
    }

    /**
     * 分页查询作品列表（前端标准接口）
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        log.info("分页查询作品 - 参数: {}", params);
        return list(params, request);
    }

    /**
     * 查询我的作品列表（学生端）
     */
    @GetMapping("/my/list")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String xuehao = (String) request.getSession().getAttribute("username");
        log.info("查询我的作品 - 学号: {}", xuehao);

        // 查询该学生的所有报名记录（包括待审核、审核通过）
        EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
        ew.eq("xuehao", xuehao);
        // 不再限制审核状态，显示所有报名

        // 添加竞赛名称筛选
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
        }

        PageUtils page = jingsaibaomingService.queryPage(params, ew);

        log.info("查询结果 - 总数: {}", page.getTotal());
        return R.ok().put("page", page);
    }

    /**
     * 查询所有作品列表（教师端）
     * 注意：教师只能查看自己创建的竞赛的作品
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        log.info("查询作品列表 - 参数: {}", params);
        
        // 权限控制：根据用户角色过滤数据
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("当前用户角色tableName：{}", tableName);

        EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
        ew.andNew("sfsh = '是' OR sfsh = '通过'"); // 兼容两种审核通过值
        ew.isNotNull("cansaizuopin"); // 只显示已提交作品的
        
        // 教师只能查看自己创建的竞赛的作品
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询作品列表", gonghao);
            
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛ID列表
                List<Long> jingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(java.util.stream.Collectors.toList());
                // 只查询这些竞赛的作品
                ew.in("jingsai_id", jingsaiIds);
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的作品", gonghao, jingsaiIds.size());
            } else {
                // 如果教师没有创建任何竞赛，返回空列表
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                return R.ok().put("data", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1))
                           .put("page", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1));
            }
        }

        // 添加竞赛名称筛选
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
        }

        // 添加学生姓名筛选
        if (params.get("xueshengxingming") != null && !params.get("xueshengxingming").toString().isEmpty()) {
            ew.like("xueshengxingming", params.get("xueshengxingming").toString());
        }

        PageUtils page = jingsaibaomingService.queryPage(params, ew);

        log.info("查询结果 - 总数: {}", page.getTotal());
        return R.ok().put("page", page);
    }

    /**
     * 【论文5.2.3】分片上传 - 上传单个分片
     * 大文件先切成5MB的小片，一片一片传上去
     *
     * @param file 分片文件数据
     * @param fileIdentifier 文件唯一标识（用于关联同一文件的各分片）
     * @param chunkIndex 当前分片索引（从0开始）
     * @param totalChunks 总分片数
     * @param originalFilename 原始文件名
     * @param totalSize 文件总大小
     * @param request HTTP请求
     * @return 上传结果
     */
    @OperationLog("分片上传作品分片")
    @PostMapping("/chunkUpload")
    public R chunkUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileIdentifier") String fileIdentifier,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("originalFilename") String originalFilename,
            @RequestParam("totalSize") long totalSize,
            HttpServletRequest request) {
        try {
            log.info("【分片上传】接收分片 - 标识:{}, 分片:{}/{}, 文件:{}, 大度:{}", 
                    fileIdentifier, chunkIndex, totalChunks, originalFilename, totalSize);

            // 验证登录
            String xuehao = (String) request.getSession().getAttribute("username");
            if (xuehao == null) {
                return R.error("请先登录");
            }

            // 验证文件类型
            if (originalFilename == null || !originalFilename.contains(".")) {
                return R.error("文件名非法");
            }
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String[] allowedExts = {"doc", "docx", "pdf", "zip", "rar", "7z", "ppt", "pptx", "xls", "xlsx"};
            boolean allowed = false;
            for (String ext : allowedExts) {
                if (ext.equals(fileExt)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                return R.error("不支持的文件格式");
            }

            // 验证文件总大小（50MB）
            if (totalSize > 50 * 1024 * 1024) {
                return R.error("文件大小不能超过50MB");
            }

            // 保存分片元数据
            if (!chunkUploadMeta.containsKey(fileIdentifier)) {
                Map<String, Object> meta = new HashMap<>();
                meta.put("originalFilename", originalFilename);
                meta.put("totalSize", totalSize);
                meta.put("totalChunks", totalChunks);
                meta.put("fileExt", fileExt);
                meta.put("xuehao", xuehao);
                chunkUploadMeta.put(fileIdentifier, meta);
            }

            // 创建分片临时存储目录
            String projectRoot = System.getProperty("user.dir");
            String chunksDir = projectRoot + "/upload/zuopin/chunks/" + fileIdentifier;
            File dir = new File(chunksDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存分片文件
            File chunkFile = new File(dir, chunkIndex + ".part");
            Files.copy(file.getInputStream(), chunkFile.toPath());

            // 记录已上传分片
            ConcurrentHashMap<Integer, Boolean> progress = chunkUploadProgress.computeIfAbsent(fileIdentifier, k -> new ConcurrentHashMap<>());
            progress.put(chunkIndex, true);

            log.info("【分片上传】分片 {} 保存成功 - 进度: {}/{}", chunkIndex, progress.size(), totalChunks);

            // 检查是否所有分片都已上传完成
            if (progress.size() == totalChunks) {
                log.info("【分片上传】所有分片已上传完成，准备合并文件: {}", fileIdentifier);
                // 合合完成后返回特殊标识
                return R.ok("分片上传完成，准备合并").put("completed", true).put("fileIdentifier", fileIdentifier);
            }

            return R.ok("分片上传成功").put("completed", false).put("chunkIndex", chunkIndex).put("uploadedChunks", progress.size());

        } catch (Exception e) {
            log.error("【分片上传】异常", e);
            return R.error("分片上传失败：" + e.getMessage());
        }
    }

    /**
     * 【论文5.2.3】断点续传 - 检查已上传的分片
     * 返回已上传的分片索引列表，用于断点续传
     *
     * @param fileIdentifier 文件唯一标识
     * @return 已上传的分片索引列表
     */
    @GetMapping("/chunkCheck")
    public R chunkCheck(@RequestParam("fileIdentifier") String fileIdentifier) {
        try {
            ConcurrentHashMap<Integer, Boolean> progress = chunkUploadProgress.get(fileIdentifier);
            List<Integer> uploadedChunks = new ArrayList<>();

            if (progress != null) {
                uploadedChunks.addAll(progress.keySet());
                log.info("【断点续传】检查已传分片 - 标识:{}, 已传分片数:{}", fileIdentifier, uploadedChunks.size());
            } else {
                // 检查磁盘上的分片文件
                String projectRoot = System.getProperty("user.dir");
                String chunksDir = projectRoot + "/upload/zuopin/chunks/" + fileIdentifier;
                File dir = new File(chunksDir);
                if (dir.exists()) {
                    for (File f : dir.listFiles()) {
                        if (f.getName().endsWith(".part")) {
                            int chunkIdx = Integer.parseInt(f.getName().replace(".part", ""));
                            uploadedChunks.add(chunkIdx);
                        }
                    }
                    log.info("【断点续传】从磁盘恢复进度 - 标识:{}, 已传分片数:{}", fileIdentifier, uploadedChunks.size());
                }
            }

            return R.ok().put("uploadedChunks", uploadedChunks);
        } catch (Exception e) {
            log.error("【断点续传】检查异常", e);
            return R.error("检查失败");
        }
    }

    /**
     * 【论文5.2.3】合并分片 - 将所有分片合并为完整文件
     *
     * @param params 包含 fileIdentifier 和 baomingId
     * @param request HTTP请求
     * @return 合合结果
     */
    @OperationLog("合并上传作品文件")
    @PostMapping("/chunkMerge")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R chunkMerge(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String fileIdentifier = (String) params.get("fileIdentifier");
            Long baomingId = Long.parseLong(params.get("baomingId").toString());

            log.info("【分片合并】开始合并 - 标识:{}, 报名ID:{}", fileIdentifier, baomingId);

            // 查询报名记录
            JingsaibaomingEntity baoming = jingsaibaomingService.selectById(baomingId);
            if (baoming == null) {
                return R.error("报名信息不存在");
            }

            // 验证是否为当前学生的报名（大小写不敏感）
            String xuehao = (String) request.getSession().getAttribute("username");
            if (xuehao == null || !xuehao.equalsIgnoreCase(baoming.getXuehao())) {
                return R.error("无权操作此报名记录");
            }

            // 验证报名是否已审核通过（兼容"是"和"通过"两种值）
            if (!"通过".equals(baoming.getSfsh()) && !"是".equals(baoming.getSfsh())) {
                return R.error("报名信息未审核通过，无法提交作品");
            }

            // 验证缴费状态
            EntityWrapper<JingsaiJiaofeiJiluEntity> jiaofeiEw = new EntityWrapper<>();
            jiaofeiEw.eq("baoming_id", baomingId);
            jiaofeiEw.eq("xuehao", baoming.getXuehao());
            List<JingsaiJiaofeiJiluEntity> jiaofeiList = jingsaiJiaofeiJiluService.selectList(jiaofeiEw);
            boolean hasApprovedPayment = false;
            if (jiaofeiList != null) {
                for (JingsaiJiaofeiJiluEntity jiaofei : jiaofeiList) {
                    if ("已通过".equals(jiaofei.getJiaofeiZhuangtai())) {
                        hasApprovedPayment = true;
                        break;
                    }
                }
            }
            if (!hasApprovedPayment) {
                return R.error("请先完成缴费并等待审核通过后，再提交作品");
            }

            // 获取分片元数据
            Map<String, Object> meta = chunkUploadMeta.get(fileIdentifier);
            if (meta == null) {
                return R.error("文件上传信息不存在，请重新上传");
            }

            int totalChunks = (int) meta.get("totalChunks");
            String fileExt = (String) meta.get("fileExt");

            // 检查所有分片是否都已上传
            ConcurrentHashMap<Integer, Boolean> progress = chunkUploadProgress.get(fileIdentifier);
            if (progress == null || progress.size() < totalChunks) {
                return R.error("还有分片未上传完成，请继续上传");
            }

            // 合并分片文件
            String projectRoot = System.getProperty("user.dir");
            String uploadPath = projectRoot + "/upload/zuopin/";
            String chunksDir = projectRoot + "/upload/zuopin/chunks/" + fileIdentifier;

            // 生成最终文件名
            String finalFileName = baoming.getXuehao() + "_" + baomingId + "_" + System.currentTimeMillis() + "." + fileExt;
            File finalFile = new File(uploadPath, finalFileName);

            // 合合操作：按分片索引顺序写入
            OutputStream outStream = new FileOutputStream(finalFile);
            byte[] buffer = new byte[CHUNK_SIZE];
            for (int i = 0; i < totalChunks; i++) {
                File chunkFile = new File(chunksDir, i + ".part");
                if (!chunkFile.exists()) {
                    outStream.close();
                    finalFile.delete();
                    return R.error("分片 " + i + " 不存在，请重新上传");
                }
                FileInputStream inStream = new FileInputStream(chunkFile);
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                inStream.close();
            }
            outStream.flush();
            outStream.close();

            log.info("【分片合并】合并完成 - 文件:{}, 大度:{}字节", finalFileName, finalFile.length());

            // 更新报名记录的作品字段
            baoming.setCansaizuopin("upload/zuopin/" + finalFileName);
            jingsaibaomingService.updateById(baoming);

            // 版本控制 - 保留最近3个版本
            try {
                cleanupOldVersions(baoming.getXuehao(), baomingId, projectRoot);
            } catch (Exception e) {
                log.warn("清理旧版本失败：{}", e.getMessage());
            }

            // 清理分片临时文件
            File chunksDirFile = new File(chunksDir);
            if (chunksDirFile.exists()) {
                for (File f : chunksDirFile.listFiles()) {
                    f.delete();
                }
                chunksDirFile.delete();
            }
            chunkUploadProgress.remove(fileIdentifier);
            chunkUploadMeta.remove(fileIdentifier);

            log.info("【分片上传】整个流程完成 - 学号:{}, 竞赛:{}, 文件:{}", 
                    baoming.getXuehao(), baoming.getJingsaimingcheng(), finalFileName);
            return R.ok("作品上传成功").put("fileName", finalFileName);

        } catch (Exception e) {
            log.error("【分片合并】异常", e);
            return R.error("合并失败：" + e.getMessage());
        }
    }

    /**
     * 上传/更新参赛作品
     */
    @OperationLog("上传参赛作品")
    @PostMapping("/upload")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R uploadZuopin(
            @RequestParam("file") MultipartFile file,
            @RequestParam("baomingId") Long baomingId,
            HttpServletRequest request) {
        try {
            log.info("========== 上传参赛作品开始 ==========");
            log.info("报名ID: {}", baomingId);
            log.info("文件名: {}", file.getOriginalFilename());

            // 1. 验证文件
            if (file.isEmpty()) {
                return R.error("请选择要上传的文件");
            }

            // 2. 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return R.error("文件名非法");
            }
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String[] allowedExts = {"doc", "docx", "pdf", "zip", "rar", "7z", "ppt", "pptx", "xls", "xlsx"};
            boolean allowed = false;
            for (String ext : allowedExts) {
                if (ext.equals(fileExt)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                return R.error("不支持的文件格式，请上传：doc、docx、pdf、zip、rar、7z、ppt、pptx、xls、xlsx");
            }

            // 3. 验证文件大小（50MB）
            if (file.getSize() > 50 * 1024 * 1024) {
                return R.error("文件大小不能超过50MB，当前文件：" + (file.getSize() / 1024 / 1024) + "MB");
            }

            // 4. 查询报名记录
            JingsaibaomingEntity baoming = jingsaibaomingService.selectById(baomingId);
            if (baoming == null) {
                return R.error("报名信息不存在");
            }

            // 5. 验证是否为当前学生的报名（大小写不敏感）
            String xuehao = (String) request.getSession().getAttribute("username");
            if (xuehao == null || !xuehao.equalsIgnoreCase(baoming.getXuehao())) {
                return R.error("无权操作此报名记录");
            }

            // 验证报名是否已审核通过（兼容"是"和"通过"两种值）
            if (!"通过".equals(baoming.getSfsh()) && !"是".equals(baoming.getSfsh())) {
                return R.error("报名信息未审核通过，无法提交作品");
            }
            
            // 【重要】7. 验证缴费状态 - 只有缴费审核通过后才能提交作品
            EntityWrapper<JingsaiJiaofeiJiluEntity> jiaofeiEw = new EntityWrapper<>();
            jiaofeiEw.eq("baoming_id", baomingId);
            jiaofeiEw.eq("xuehao", baoming.getXuehao());
            List<JingsaiJiaofeiJiluEntity> jiaofeiList = jingsaiJiaofeiJiluService.selectList(jiaofeiEw);
            
            boolean hasApprovedPayment = false;
            if (jiaofeiList != null && !jiaofeiList.isEmpty()) {
                for (JingsaiJiaofeiJiluEntity jiaofei : jiaofeiList) {
                    if ("已通过".equals(jiaofei.getJiaofeiZhuangtai())) {
                        hasApprovedPayment = true;
                        break;
                    }
                }
            }
            
            if (!hasApprovedPayment) {
                log.warn("学生 {} 尝试提交作品但未缴费或未通过审核，报名ID: {}", baoming.getXuehao(), baomingId);
                return R.error("请先完成缴费并等待教师审核通过后，再提交作品");
            }

            // 【论文3.1.1(4)】8. 保存文件 - 支持版本控制，保留最近3个版本
            String projectRoot = System.getProperty("user.dir");
            String uploadPath = projectRoot + "/upload/zuopin/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名：学号_报名ID_时间戳.扩展名
            String fileName = baoming.getXuehao() + "_" + baomingId + "_" + System.currentTimeMillis() + "." + fileExt;
            File destFile = new File(uploadDir, fileName);
            Files.copy(file.getInputStream(), destFile.toPath());

            // 8. 更新报名记录的作品字段
            String oldFile = baoming.getCansaizuopin();
            baoming.setCansaizuopin("upload/zuopin/" + fileName);
            jingsaibaomingService.updateById(baoming);

            // 【论文3.1.1(4)】9. 版本控制 - 保留最近3个版本，超出部分删除
            // 不直接删除旧文件，而是保留作为版本历史
            try {
                cleanupOldVersions(baoming.getXuehao(), baomingId, projectRoot);
            } catch (Exception e) {
                log.warn("清理旧版本文件失败：{}", e.getMessage());
            }

            log.info("学生 {} 上传作品成功，竞赛：{}，文件：{}",
                    baoming.getXuehao(), baoming.getJingsaimingcheng(), fileName);
            return R.ok("作品上传成功");
        } catch (Exception e) {
            log.error("上传参赛作品异常", e);
            return R.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除已提交的作品
     */
    @OperationLog("删除参赛作品")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R deleteZuopin(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long baomingId = Long.parseLong(params.get("baomingId").toString());
            log.info("删除作品 - 报名ID: {}", baomingId);

            // 查询报名记录
            JingsaibaomingEntity baoming = jingsaibaomingService.selectById(baomingId);
            if (baoming == null) {
                return R.error("报名信息不存在");
            }

            // 验证是否为当前学生的报名（大小写不敏感）
            String xuehao = (String) request.getSession().getAttribute("username");
            if (xuehao == null || !xuehao.equalsIgnoreCase(baoming.getXuehao())) {
                return R.error("无权操作此报名记录");
            }

            // 清空作品字段
            baoming.setCansaizuopin("");
            jingsaibaomingService.updateById(baoming);

            log.info("学生 {} 删除作品成功", xuehao);
            return R.ok("作品删除成功");
        } catch (Exception e) {
            log.error("删除作品异常", e);
            return R.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 统计作品提交情况
     * 教师只能统计自己创建的竞赛的作品提交情况
     */
    @GetMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        try {
            log.info("========== 作品统计数据查询开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.info("当前用户角色tableName：{}", tableName);
            
            Map<String, Object> data = new HashMap<>();
            
            if ("jiaoshi".equals(tableName)) {
                // 教师端：只统计自己创建的竞赛的作品提交情况
                String gonghao = (String) request.getSession().getAttribute("username");
                log.info("教师角色，工号：{}", gonghao);
                
                // 查询该教师创建的所有竞赛ID
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                int totalBaoming = 0;
                int submittedCount = 0;
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    // 提取竞赛ID列表
                    List<Long> jingsaiIds = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getId)
                            .collect(java.util.stream.Collectors.toList());
                    
                    // 查询这些竞赛的所有报名记录
                    EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                    baomingEw.in("jingsai_id", jingsaiIds);
                    baomingEw.andNew("sfsh = '是' OR sfsh = '通过'");
                    List<JingsaibaomingEntity> myBaomings = jingsaibaomingService.selectList(baomingEw);
                    
                    // 统计已提交作品的
                    if (myBaomings != null) {
                        totalBaoming = myBaomings.size();
                        for (JingsaibaomingEntity baoming : myBaomings) {
                            if (baoming.getCansaizuopin() != null && !baoming.getCansaizuopin().isEmpty()) {
                                submittedCount++;
                            }
                        }
                    }
                    
                    log.info("教师 {} 创建了 {} 个竞赛，已审核报名数：{}，已提交作品：{}，未提交：{}", 
                            gonghao, jingsaiIds.size(), totalBaoming, submittedCount, totalBaoming - submittedCount);
                } else {
                    log.info("教师 {} 还没有创建任何竞赛", gonghao);
                }
                
                data.put("totalBaoming", totalBaoming);
                data.put("submittedCount", submittedCount);
                data.put("unsubmittedCount", totalBaoming - submittedCount);
                
            } else if ("xuesheng".equals(tableName)) {
                // 学生端：统计自己的作品提交情况
                String xuehao = (String) request.getSession().getAttribute("username");
                log.info("学生角色，学号：{}", xuehao);
                
                EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                baomingEw.eq("xuehao", xuehao);
                List<JingsaibaomingEntity> myBaomings = jingsaibaomingService.selectList(baomingEw);
                
                int totalBaoming = 0;
                int submittedCount = 0;
                
                if (myBaomings != null) {
                    totalBaoming = myBaomings.size();
                    for (JingsaibaomingEntity baoming : myBaomings) {
                        if (baoming.getCansaizuopin() != null && !baoming.getCansaizuopin().isEmpty()) {
                            submittedCount++;
                        }
                    }
                }
                
                log.info("学生 {} 已审核报名数：{}，已提交作品：{}，未提交：{}", 
                        xuehao, totalBaoming, submittedCount, totalBaoming - submittedCount);
                data.put("totalBaoming", totalBaoming);
                data.put("submittedCount", submittedCount);
                data.put("unsubmittedCount", totalBaoming - submittedCount);
                
            } else {
                // 管理员端：统计所有审核通过的报名的作品提交情况
                EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                baomingEw.andNew("sfsh = '是' OR sfsh = '通过'");
                List<JingsaibaomingEntity> allBaomings = jingsaibaomingService.selectList(baomingEw);
                
                int totalBaoming = 0;
                int submittedCount = 0;
                
                if (allBaomings != null) {
                    totalBaoming = allBaomings.size();
                    for (JingsaibaomingEntity baoming : allBaomings) {
                        if (baoming.getCansaizuopin() != null && !baoming.getCansaizuopin().isEmpty()) {
                            submittedCount++;
                        }
                    }
                }
                
                log.info("管理员端 - 已审核报名数：{}，已提交作品：{}，未提交：{}", 
                        totalBaoming, submittedCount, totalBaoming - submittedCount);
                data.put("totalBaoming", totalBaoming);
                data.put("submittedCount", submittedCount);
                data.put("unsubmittedCount", totalBaoming - submittedCount);
            }
            
            return R.ok().put("data", data);
        } catch (Exception e) {
            log.error("作品统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }
}
