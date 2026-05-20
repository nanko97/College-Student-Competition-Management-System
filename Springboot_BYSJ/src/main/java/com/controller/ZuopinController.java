










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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ew.eq("sfsh", "通过"); // 只显示审核通过的报名
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

            // 5. 验证是否为当前学生的报名
            String xuehao = (String) request.getSession().getAttribute("username");
            if (!xuehao.equals(baoming.getXuehao())) {
                return R.error("无权操作此报名记录");
            }

            // 6. 验证报名是否已审核通过
            if (!"通过".equals(baoming.getSfsh())) {
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

            // 8. 保存文件
            String projectRoot = System.getProperty("user.dir");
            String uploadPath = projectRoot + "/upload/zuopin/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名：学号_竞赛ID_时间戳_原文件名
            String fileName = baoming.getXuehao() + "_" + baomingId + "_" + System.currentTimeMillis() + "." + fileExt;
            File destFile = new File(uploadDir, fileName);
            Files.copy(file.getInputStream(), destFile.toPath());

            // 8. 更新报名记录的作品字段
            String oldFile = baoming.getCansaizuopin();
            baoming.setCansaizuopin("upload/zuopin/" + fileName);
            jingsaibaomingService.updateById(baoming);

            // 9. 如果是更新作品，删除旧文件
            if (oldFile != null && !oldFile.isEmpty()) {
                try {
                    File oldFileObj = new File(projectRoot, oldFile);
                    if (oldFileObj.exists()) {
                        oldFileObj.delete();
                        log.info("已删除旧作品文件：{}", oldFile);
                    }
                } catch (Exception e) {
                    log.warn("删除旧作品文件失败：{}", oldFile, e);
                }
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

            // 验证是否为当前学生的报名
            String xuehao = (String) request.getSession().getAttribute("username");
            if (!xuehao.equals(baoming.getXuehao())) {
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
                    baomingEw.eq("sfsh", "通过");
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
                baomingEw.eq("sfsh", "通过");
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
