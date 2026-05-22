package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dao.JiaoshiDao;
import com.entity.JiaoshiEntity;
import com.entity.JingsaiFeiyongEntity;
import com.entity.JingsaiJiaofeiJiluEntity;
import com.entity.JingsaibaomingEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiFeiyongService;
import com.service.JingsaiJiaofeiJiluService;
import com.service.JingsaibaomingService;
import com.service.JingsaixinxiService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 竞赛缴费管理控制器
 * 功能：配置竞赛费用、提交缴费凭证、审核缴费记录
 */
@RestController
@RequestMapping("/jingsai/jiaofei")
@Slf4j
public class JingsaiJiaofeiController {

    @Autowired
    private JingsaiFeiyongService jingsaiFeiyongService;

    @Autowired
    private JingsaiJiaofeiJiluService jingsaiJiaofeiJiluService;

    @Autowired
    private JingsaibaomingService jingsaibaomingService;

    @Autowired
    private JingsaixinxiService jingsaixinxiService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private JiaoshiDao jiaoshiDao;
    
    @Autowired
    private com.service.XiaoxiTongzhiService xiaoxiTongzhiService;

    /**
     * 配置竞赛费用
     */
    @OperationLog("配置竞赛费用")
    @PostMapping("/config/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R saveConfig(@RequestBody JingsaiFeiyongEntity feiyong, HttpServletRequest request) {
        try {
            // 检查是否已存在配置
            JingsaiFeiyongEntity exist = jingsaiFeiyongService.getByJingsaiId(feiyong.getJingsaiId());
            if (exist != null) {
                // 更新现有配置
                exist.setBaomingfei(feiyong.getBaomingfei());
                exist.setShifouShoufei(feiyong.getShifouShoufei());
                exist.setJiaofeiJiezhiRiqi(feiyong.getJiaofeiJiezhiRiqi());
                exist.setBeizhu(feiyong.getBeizhu());
                jingsaiFeiyongService.updateById(exist);
                return R.ok("费用配置更新成功");
            } else {
                // 新增配置
                feiyong.setId(IdWorker.getId());
                jingsaiFeiyongService.insert(feiyong);
                return R.ok("费用配置保存成功");
            }
        } catch (Exception e) {
            log.error("配置竞赛费用异常", e);
            return R.error("配置失败，请重试");
        }
    }

    /**
     * 查询竞赛费用配置
     */
    @IgnoreAuth
    @GetMapping("/config/info/{jingsaiId}")
    public R getConfig(@PathVariable Long jingsaiId) {
        JingsaiFeiyongEntity feiyong = jingsaiFeiyongService.getByJingsaiId(jingsaiId);
        if (feiyong == null) {
            return R.ok().put("data", null);
        }
        return R.ok().put("data", feiyong);
    }

    /**
     * 提交缴费凭证
     */
    @OperationLog("提交竞赛缴费凭证")
    @PostMapping("/submit")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R submitJiaofei(@RequestBody JingsaiJiaofeiJiluEntity jiaofeiJilu, HttpServletRequest request) {
        try {
            // 1. 验证报名信息
            JingsaibaomingEntity baoming = jingsaibaomingService.selectById(jiaofeiJilu.getBaomingId());
            if (baoming == null) {
                return R.error("报名信息不存在");
            }

            // 2. 检查是否已缴费
            EntityWrapper<JingsaiJiaofeiJiluEntity> ew = new EntityWrapper<>();
            ew.eq("baoming_id", jiaofeiJilu.getBaomingId());
            Integer count = jingsaiJiaofeiJiluService.selectCount(ew);
            if (count > 0) {
                return R.error("该报名已提交缴费，请勿重复提交");
            }

            // 3. 获取竞赛信息验证缴费
            JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(jiaofeiJilu.getJingsaiId());
            if (jingsai == null) {
                return R.error("竞赛信息不存在");
            }
            if ("免费".equals(jingsai.getJiaofeimoshi())) {
                return R.error("该竞赛无需缴费（免费竞赛）");
            }
            BigDecimal feiyong = jingsai.getJingsaiFeiyong();
            if (feiyong == null || feiyong.compareTo(BigDecimal.ZERO) <= 0) {
                return R.error("该竞赛未配置费用金额，请联系管理员");
            }

            // 4. 验证缴费截止日期（从jingsai_feiyong表获取）
            JingsaiFeiyongEntity feiyongConfig = jingsaiFeiyongService.getByJingsaiId(jiaofeiJilu.getJingsaiId());
            if (feiyongConfig != null && feiyongConfig.getJiaofeiJiezhiRiqi() != null) {
                Date now = new Date();
                if (now.after(feiyongConfig.getJiaofeiJiezhiRiqi())) {
                    return R.error("已超过缴费截止日期（" + 
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(feiyongConfig.getJiaofeiJiezhiRiqi()) + 
                        "），无法提交缴费");
                }
            }

            // 5. 验证金额
            if (jiaofeiJilu.getJiaofeiJine().compareTo(feiyong) != 0) {
                return R.error("缴费金额不正确，应为：" + feiyong);
            }

            // 6. 保存缴费记录
            jiaofeiJilu.setId(IdWorker.getId());
            jiaofeiJilu.setJiaofeiZhuangtai("已缴费");
            jiaofeiJilu.setJiaofeiShijian(new Date());
            jiaofeiJilu.setXuehao(baoming.getXuehao());
            jiaofeiJilu.setXueshengxingming(baoming.getXueshengxingming());
            jingsaiJiaofeiJiluService.insert(jiaofeiJilu);

            // 7. 更新报名表的支付状态
            baoming.setIspay("待审核");
            jingsaibaomingService.updateById(baoming);

            log.info("学生 {} 提交缴费凭证，竞赛：{}，金额：{}",
                    baoming.getXuehao(), baoming.getJingsaimingcheng(), jiaofeiJilu.getJiaofeiJine());
            return R.ok("缴费凭证提交成功，等待审核");
        } catch (Exception e) {
            log.error("提交缴费凭证异常", e);
            return R.error("提交失败，请重试");
        }
    }

    /**
     * 查询我的缴费记录
     */
    @GetMapping("/my/list")
    public R mylist(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String xuehao = (String) request.getSession().getAttribute("username");
        log.info("查询我的缴费记录 - 学号: {}, 筛选条件: {}", xuehao, params);
        
        EntityWrapper<JingsaiJiaofeiJiluEntity> ew = new EntityWrapper<>();
        ew.eq("xuehao", xuehao);
        
        // 添加竞赛名称模糊筛选
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
            log.info("添加竞赛名称筛选: {}", params.get("jingsaimingcheng"));
        }
        
        // 添加缴费状态精确筛选
        if (params.get("jiaofeiZhuangtai") != null && !params.get("jiaofeiZhuangtai").toString().isEmpty()) {
            ew.eq("jiaofei_zhuangtai", params.get("jiaofeiZhuangtai").toString());
            log.info("添加缴费状态筛选: {}", params.get("jiaofeiZhuangtai"));
        }
        
        PageUtils page = jingsaiJiaofeiJiluService.queryPage(params, ew);
        
        // 补充费用信息（应缴金额、缴费截止日期）
        @SuppressWarnings("unchecked")
        List<JingsaiJiaofeiJiluEntity> list = (List<JingsaiJiaofeiJiluEntity>) page.getList();
        if (list != null && !list.isEmpty()) {
            for (JingsaiJiaofeiJiluEntity record : list) {
                if (record.getJingsaiId() != null) {
                    JingsaiFeiyongEntity feiyong = jingsaiFeiyongService.getByJingsaiId(record.getJingsaiId());
                    if (feiyong != null) {
                        record.setBaomingfei(feiyong.getBaomingfei());
                        record.setJiaofeiJiezhiRiqi(feiyong.getJiaofeiJiezhiRiqi());
                    }
                }
            }
        }
        
        log.info("查询结果 - 总数: {}, 列表大小: {}", page.getTotal(), page.getList().size());
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 分页查询待审核缴费（教师/管理员）
     */
    @GetMapping("/shenhe/page")
    public R shenhePage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 获取当前登录用户的工号
        String gonghao = (String) request.getSession().getAttribute("username");
        String role = (String) request.getSession().getAttribute("role");
        
        EntityWrapper<JingsaiJiaofeiJiluEntity> ew = new EntityWrapper<>();
        
        // 【重要】教师只能查看自己创建的竞赛的缴费记录，管理员可以查看所有
        if ("教师".equals(role)) {
            // 查询该教师创建的所有竞赛ID（忽略大小写）
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            // 使用 where 条件过滤工号（忽略大小写）
            jingsaiEw.eq("UPPER(gonghao)", gonghao.toUpperCase());
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            log.info("教师 {} 查询到 {} 个竞赛", gonghao, myJingsaiList != null ? myJingsaiList.size() : 0);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛ID列表
                List<Long> jingsaiIds = new java.util.ArrayList<>();
                for (JingsaixinxiEntity jingsai : myJingsaiList) {
                    jingsaiIds.add(jingsai.getId());
                }
                // 只查询这些竞赛的缴费记录
                ew.in("jingsai_id", jingsaiIds);
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的缴费记录，竞赛IDs: {}", gonghao, jingsaiIds.size(), jingsaiIds);
            } else {
                // 如果教师没有创建任何竞赛，返回空列表
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                return R.ok().put("data", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1))
                        .put("page", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1));
            }
        }
        // 管理员不添加限制，可以查看所有缴费记录
        
        // 添加搜索条件
        if (params.get("xueshengxingming") != null && !params.get("xueshengxingming").toString().isEmpty()) {
            ew.like("xueshengxingming", params.get("xueshengxingming").toString());
        }
        if (params.get("jiaofeiZhuangtai") != null && !params.get("jiaofeiZhuangtai").toString().isEmpty()) {
            ew.eq("jiaofei_zhuangtai", params.get("jiaofeiZhuangtai").toString());
        }

        PageUtils page = jingsaiJiaofeiJiluService.queryPage(params, ew);
        
        // 补充费用信息
        @SuppressWarnings("unchecked")
        List<JingsaiJiaofeiJiluEntity> list = (List<JingsaiJiaofeiJiluEntity>) page.getList();
        if (list != null && !list.isEmpty()) {
            for (JingsaiJiaofeiJiluEntity record : list) {
                if (record.getJingsaiId() != null) {
                    JingsaiFeiyongEntity feiyong = jingsaiFeiyongService.getByJingsaiId(record.getJingsaiId());
                    if (feiyong != null) {
                        record.setBaomingfei(feiyong.getBaomingfei());
                        record.setJiaofeiJiezhiRiqi(feiyong.getJiaofeiJiezhiRiqi());
                    }
                }
            }
        }
        
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 审核通过缴费
     */
    @OperationLog("通过竞赛缴费审核")
    @PostMapping("/shenhe/approve")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R approveJiaofei(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long jiaofeiId = Long.parseLong(params.get("jiaofeiId").toString());
            String yijian = params.get("yijian") != null ? params.get("yijian").toString() : "";
            String gonghao = (String) request.getSession().getAttribute("username");
            // 获取教师姓名
            String shenheRen = gonghao;
            JiaoshiEntity jiaoshi = jiaoshiDao.selectById(gonghao);
            if (jiaoshi != null && jiaoshi.getJiaoshixingming() != null) {
                shenheRen = jiaoshi.getJiaoshixingming();
            }
            log.info("审核通过缴费 - 工号: {}, 姓名: {}", gonghao, shenheRen);

            R result = jingsaiJiaofeiJiluService.shenheJiaofei(jiaofeiId, "已通过", yijian, shenheRen);
            // 消息通知已由Service层统一发送
            
            return result;
        } catch (Exception e) {
            log.error("审核缴费异常", e);
            return R.error("审核失败，请重试");
        }
    }

    /**
     * 审核驳回缴费
     */
    @OperationLog("驳回竞赛缴费审核")
    @PostMapping("/shenhe/reject")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R rejectJiaofei(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long jiaofeiId = Long.parseLong(params.get("jiaofeiId").toString());
            String yijian = params.get("yijian") != null ? params.get("yijian").toString() : "缴费不符合要求";
            String gonghao = (String) request.getSession().getAttribute("username");
            // 获取教师姓名
            String shenheRen = gonghao;
            JiaoshiEntity jiaoshi = jiaoshiDao.selectById(gonghao);
            if (jiaoshi != null && jiaoshi.getJiaoshixingming() != null) {
                shenheRen = jiaoshi.getJiaoshixingming();
            }
            log.info("审核驳回缴费 - 工号: {}, 姓名: {}", gonghao, shenheRen);

            R result = jingsaiJiaofeiJiluService.shenheJiaofei(jiaofeiId, "已驳回", yijian, shenheRen);
            // 消息通知已由Service层统一发送
            
            return result;
        } catch (Exception e) {
            log.error("驳回缴费异常", e);
            return R.error("操作失败，请重试");
        }
    }

    /**
     * 上传缴费凭证（支持文件上传）
     */
    @IgnoreAuth
    @PostMapping("/upload-pingzheng")
    public R uploadPingzheng(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jiaofeiId") Long jiaofeiId,
            @RequestParam(value = "beizhu", required = false) String beizhu,
            HttpServletRequest request) {
        try {
            log.info("========== 上传缴费凭证开始 ==========");
            log.info("接收到的jiaofeiId: {}", jiaofeiId);
            log.info("文件名: {}", file.getOriginalFilename());
            
            // 1. 验证文件
            if (file.isEmpty()) {
                return R.error("请选择要上传的文件");
            }

            // 2. 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return R.error("文件大小不能超过5MB");
            }

            // 3. 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return R.error("文件名非法");
            }
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!"jpg".equals(fileExt) && !"png".equals(fileExt) && !"jpeg".equals(fileExt)) {
                return R.error("仅支持jpg、png格式的图片");
            }

            // 4. 查询缴费记录
            JingsaiJiaofeiJiluEntity jiaofeiJilu = jingsaiJiaofeiJiluService.selectById(jiaofeiId);
            if (jiaofeiJilu == null) {
                return R.error("缴费记录不存在");
            }

            // 5. 检查缴费状态，未缴费或已驳回状态允许上传
            String status = jiaofeiJilu.getJiaofeiZhuangtai();
            if (!"未缴费".equals(status) && !"已驳回".equals(status)) {
                return R.error("当前状态不允许上传凭证");
            }

            // 6. 保存文件
            String projectRoot = System.getProperty("user.dir");
            // 统一保存到项目根目录的 upload 文件夹
            String uploadPath = projectRoot + "/upload/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            File destFile = new File(uploadDir, fileName);
            Files.copy(file.getInputStream(), destFile.toPath());

            // 7. 更新缴费记录 - 保存相对路径，前端通过 /upload/ 访问
            jiaofeiJilu.setPingzhengTupian("upload/" + fileName);
            jiaofeiJilu.setJiaofeiZhuangtai("已缴费");
            jiaofeiJilu.setJiaofeiShijian(new Date());
            jingsaiJiaofeiJiluService.updateById(jiaofeiJilu);

            // 8. 更新报名表的支付状态
            JingsaibaomingEntity baoming = jingsaibaomingService.selectById(jiaofeiJilu.getBaomingId());
            if (baoming != null) {
                baoming.setIspay("待审核");
                jingsaibaomingService.updateById(baoming);
            }

            log.info("学生 {} 上传缴费凭证成功，竞赛：{}，文件：{}",
                    jiaofeiJilu.getXuehao(), jiaofeiJilu.getJingsaimingcheng(), fileName);

            // 发送缴费凭证上传通知给负责教师
            try {
                JingsaibaomingEntity baoming2 = jingsaibaomingService.selectById(jiaofeiJilu.getBaomingId());
                if (baoming2 != null) {
                    EntityWrapper<JingsaixinxiEntity> jingsaiEw2 = new EntityWrapper<>();
                    jingsaiEw2.eq("jingsaimingcheng", baoming2.getJingsaimingcheng());
                    JingsaixinxiEntity jingsai2 = jingsaixinxiService.selectOne(jingsaiEw2);
                    if (jingsai2 != null && jingsai2.getGonghao() != null && !jingsai2.getGonghao().isEmpty()) {
                        String teacherGonghao = jingsai2.getGonghao();
                        String studentName = jiaofeiJilu.getXueshengxingming() != null ? jiaofeiJilu.getXueshengxingming() : jiaofeiJilu.getXuehao();
                        String competitionName = jiaofeiJilu.getJingsaimingcheng();
                        String title = "缴费凭证提交";
                        String content = String.format("学生「%s」提交了「%s」竞赛的缴费凭证，请及时审核。", studentName, competitionName);
                        xiaoxiTongzhiService.sendTongzhi(
                            title, content, "缴费审核", "系统",
                            null, teacherGonghao, "jiaoshi",
                            jiaofeiId, "jiaofei"
                        );
                        log.info("发送缴费凭证上传通知给教师: {}, 缴费ID: {}", teacherGonghao, jiaofeiId);
                    }
                }
            } catch (Exception e) {
                log.error("发送缴费凭证上传通知异常", e);
            }

            return R.ok("缴费凭证上传成功，等待审核");
        } catch (Exception e) {
            log.error("上传缴费凭证异常", e);
            return R.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 统计缴费情况（不需要jingsaiId参数，根据角色返回统计数据）
     */
    @GetMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        try {
            log.info("========== 缴费统计数据查询开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            String username = (String) request.getSession().getAttribute("username");
            
            log.info("缴费统计数据查询 - 角色:{}, 用户:{}", tableName, username);
            
            // 使用 EntityWrapper + selectCount 避免全表扫描
            List<Long> jingsaiIds = null;
            
            // 如果是教师，过滤出该教师的竞赛的缴费记录
            if ("jiaoshi".equals(tableName)) {
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", username);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    jingsaiIds = new java.util.ArrayList<>();
                    for (JingsaixinxiEntity jingsai : myJingsaiList) {
                        jingsaiIds.add(jingsai.getId());
                    }
                    log.info("教师 {} 统计自己创建的 {} 个竞赛的缴费记录", username, jingsaiIds.size());
                } else {
                    Map<String, Object> stats = new java.util.HashMap<>();
                    stats.put("total", 0);
                    stats.put("yitongguo", 0);
                    stats.put("daishenhe", 0);
                    return R.ok().put("data", stats);
                }
            }

            // 统计总数
            EntityWrapper<JingsaiJiaofeiJiluEntity> baseEw = new EntityWrapper<>();
            if (jingsaiIds != null) baseEw.in("jingsai_id", jingsaiIds);
            int total = jingsaiJiaofeiJiluService.selectCount(baseEw);
            log.info("缴费记录总数：{}", total);

            // 统计已通过数量
            EntityWrapper<JingsaiJiaofeiJiluEntity> tongguoEw = new EntityWrapper<>();
            tongguoEw.eq("jiaofei_zhuangtai", "已通过");
            if (jingsaiIds != null) tongguoEw.in("jingsai_id", jingsaiIds);
            int yitongguo = jingsaiJiaofeiJiluService.selectCount(tongguoEw);
            
            // 统计待审核数量
            EntityWrapper<JingsaiJiaofeiJiluEntity> shenheEw = new EntityWrapper<>();
            shenheEw.eq("jiaofei_zhuangtai", "已缴费");
            if (jingsaiIds != null) shenheEw.in("jingsai_id", jingsaiIds);
            int daishenhe = jingsaiJiaofeiJiluService.selectCount(shenheEw);
            log.info("已通过：{}，待审核：{}", yitongguo, daishenhe);

            Map<String, Object> data = new java.util.HashMap<>();
            data.put("total", total);
            data.put("yitongguo", yitongguo);
            data.put("daishenhe", daishenhe);
            
            log.info("缴费统计数据查询成功 - 总数:{}, 已通过:{}, 待审核:{}", total, yitongguo, daishenhe);
            return R.ok().put("data", data);
            
        } catch (Exception e) {
            log.error("缴费统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }
}
