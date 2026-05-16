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

            // 3. 获取费用配置
            JingsaiFeiyongEntity feiyong = jingsaiFeiyongService.getByJingsaiId(jiaofeiJilu.getJingsaiId());
            if (feiyong == null || "否".equals(feiyong.getShifouShoufei())) {
                return R.error("该竞赛无需缴费");
            }

            // 4. 验证缴费截止日期
            if (feiyong.getJiaofeiJiezhiRiqi() != null) {
                Date now = new Date();
                if (now.after(feiyong.getJiaofeiJiezhiRiqi())) {
                    return R.error("已超过缴费截止日期（" + 
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(feiyong.getJiaofeiJiezhiRiqi()) + 
                        "），无法提交缴费");
                }
            }

            // 5. 验证金额
            if (jiaofeiJilu.getJiaofeiJine().compareTo(feiyong.getBaomingfei()) != 0) {
                return R.error("缴费金额不正确，应为：" + feiyong.getBaomingfei());
            }

            // 6. 保存缴费记录
            jiaofeiJilu.setId(IdWorker.getId());
            jiaofeiJilu.setJiaofeiZhuangtai("已缴费");
            jiaofeiJilu.setJiaofeiShijian(new Date());
            jiaofeiJilu.setXuehao(baoming.getXuehao());
            jiaofeiJilu.setXueshengxingming(baoming.getXueshengxingming());
            jingsaiJiaofeiJiluService.insert(jiaofeiJilu);

            // 7. 更新报名表的支付状态
            baoming.setIspay("已缴费");
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
                baoming.setIspay("已缴费");
                jingsaibaomingService.updateById(baoming);
            }

            log.info("学生 {} 上传缴费凭证成功，竞赛：{}，文件：{}",
                    jiaofeiJilu.getXuehao(), jiaofeiJilu.getJingsaimingcheng(), fileName);
            return R.ok("缴费凭证上传成功，等待审核");
        } catch (Exception e) {
            log.error("上传缴费凭证异常", e);
            return R.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 统计缴费情况
     */
    @GetMapping("/statistics/{jingsaiId}")
    public R statistics(@PathVariable Long jingsaiId) {
        EntityWrapper<JingsaiJiaofeiJiluEntity> ew = new EntityWrapper<>();
        ew.eq("jingsai_id", jingsaiId);

        int total = jingsaiJiaofeiJiluService.selectCount(ew);

        EntityWrapper<JingsaiJiaofeiJiluEntity> ewYitongguo = new EntityWrapper<>();
        ewYitongguo.eq("jingsai_id", jingsaiId);
        ewYitongguo.eq("jiaofei_zhuangtai", "已通过");
        int yitongguo = jingsaiJiaofeiJiluService.selectCount(ewYitongguo);

        EntityWrapper<JingsaiJiaofeiJiluEntity> ewDaishenhe = new EntityWrapper<>();
        ewDaishenhe.eq("jingsai_id", jingsaiId);
        ewDaishenhe.eq("jiaofei_zhuangtai", "已缴费");
        int daishenhe = jingsaiJiaofeiJiluService.selectCount(ewDaishenhe);

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("total", total);
        data.put("yitongguo", yitongguo);
        data.put("daishenhe", daishenhe);

        return R.ok().put("data", data);
    }
}
