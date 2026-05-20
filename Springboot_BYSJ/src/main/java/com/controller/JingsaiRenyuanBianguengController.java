package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.dao.JiaoshiDao;
import com.entity.JiaoshiEntity;
import com.entity.JingsaiRenyuanBianguengEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiRenyuanBianguengService;
import com.service.JingsaixinxiService;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jingsai/biangueng")
@Slf4j
public class JingsaiRenyuanBianguengController {
    @Autowired private JingsaiRenyuanBianguengService bianguengService;
    @Autowired private JiaoshiDao jiaoshiDao;
    @Autowired private JingsaixinxiService jingsaixinxiService;
    @Autowired private com.service.XiaoxiTongzhiService xiaoxiTongzhiService;

    /**
     * 分页查询变更记录（通用）
     * 教师只能查看自己组织的竞赛的变更记录
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 权限控制：根据用户角色过滤数据
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("查询人员变更记录 - 角色: {}", tableName);
        
        // 教师只能查看自己组织的竞赛的变更记录
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询自己组织的竞赛的人员变更记录", gonghao);
            
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛 ID 列表
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(java.util.stream.Collectors.toList());
                
                // 将竞赛ID列表添加到params中，供Service层过滤
                params.put("jingsai_id_in", myJingsaiIds);
                log.info("教师 {} 查询到 {} 个竞赛的人员变更记录", gonghao, myJingsaiIds.size());
            } else {
                // 该教师没有创建任何竞赛，返回空结果
                params.put("jingsai_id", -1);
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
            }
        }
        
        PageUtils page = bianguengService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询变更记录列表
     * 教师只能查看自己组织的竞赛的变更记录
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 权限控制：根据用户角色过滤数据
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("查询人员变更记录列表 - 角色: {}", tableName);
        
        // 教师只能查看自己组织的竞赛的变更记录
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询自己组织的竞赛的人员变更记录列表", gonghao);
            
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛 ID 列表
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(java.util.stream.Collectors.toList());
                
                // 将竞赛ID列表添加到params中，供Service层过滤
                params.put("jingsai_id_in", myJingsaiIds);
                log.info("教师 {} 查询到 {} 个竞赛的人员变更记录列表", gonghao, myJingsaiIds.size());
            } else {
                // 该教师没有创建任何竞赛，返回空结果
                params.put("jingsai_id", -1);
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
            }
        }
        
        PageUtils page = bianguengService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询变更详情
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().put("biangueng", bianguengService.selectById(id));
    }

    /**
     * 删除变更记录
     */
    @OperationLog("删除人员变更记录")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        bianguengService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok("删除成功");
    }

    @OperationLog("申请人贝变更")
    @PostMapping("/apply")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R apply(@RequestBody JingsaiRenyuanBianguengEntity biangueng, HttpServletRequest request) {
        String caozuoRen = (String) request.getSession().getAttribute("username");
        R result = bianguengService.applyBiangueng(biangueng, caozuoRen);

        // 发送人员变更申请通知给负责教师
        try {
            if ((Integer) result.get("code") == 0 && biangueng.getJingsaiId() != null) {
                JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(biangueng.getJingsaiId());
                if (jingsai != null && jingsai.getGonghao() != null && !jingsai.getGonghao().isEmpty()) {
                    String teacherGonghao = jingsai.getGonghao();
                    String applicantName = biangueng.getCaozuoRenXingming() != null ? biangueng.getCaozuoRenXingming() : caozuoRen;
                    String bianguengType = biangueng.getBianguengLeixing();
                    String tuanduiBianhao = biangueng.getTuanduiBianhao() != null ? biangueng.getTuanduiBianhao() : "";
                    String title = "人员变更申请";
                    String content = String.format("学生「%s」提交了团队「%s」的%s申请，请及时审核。", applicantName, tuanduiBianhao, bianguengType);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "人员变更", "系统",
                        null, teacherGonghao, "jiaoshi",
                        biangueng.getId(), "renyuanbiangueng"
                    );
                    log.info("发送人员变更申请通知给教师: {}, 变更ID: {}", teacherGonghao, biangueng.getId());
                }
            }
        } catch (Exception e) {
            log.error("发送人员变更申请通知异常", e);
        }

        return result;
    }

    /**
     * 分页查询待审核变更记录（教师/管理员）
     * 教师只能审核自己创建的竞赛的人员变更申请
     */
    @GetMapping("/shenhe/page")
    public R shenhePage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 获取当前用户信息
        String gonghao = (String) request.getSession().getAttribute("username");
        String role = (String) request.getSession().getAttribute("role");
        
        EntityWrapper<JingsaiRenyuanBianguengEntity> ew = new EntityWrapper<>();
        
        // 支持按审核状态筛选，默认查待审核
        String shenheStatus = params.get("shenheZhuangtai") != null ? params.get("shenheZhuangtai").toString() : "待审核";
        if (!shenheStatus.isEmpty()) {
            ew.eq("shenhe_zhuangtai", shenheStatus);
        }
        
        // 【重要】教师只能查看自己创建的竞赛的人员变更申请，管理员可以查看所有
        if ("教师".equals(role)) {
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            log.info("教师 {} 查询到 {} 个竞赛", gonghao, myJingsaiList != null ? myJingsaiList.size() : 0);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛ID列表
                List<Long> jingsaiIds = new java.util.ArrayList<>();
                for (JingsaixinxiEntity jingsai : myJingsaiList) {
                    jingsaiIds.add(jingsai.getId());
                }
                // 只查询这些竞赛的人员变更申请
                ew.in("jingsai_id", jingsaiIds);
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的人员变更申请，竞赛IDs: {}", gonghao, jingsaiIds.size(), jingsaiIds);
            } else {
                // 如果教师没有创建任何竞赛，返回空列表
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                return R.ok().put("page", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1));
            }
        }
        // 管理员不添加限制，可以查看所有申请
        
        PageUtils page = bianguengService.queryPage(params, ew);
        log.info("查询待审核人员变更 - 总数: {}, 列表大小: {}", page.getTotal(), page.getList().size());
        return R.ok().put("page", page);
    }

    @OperationLog("通过人员变更申请")
    @PostMapping("/shenhe/approve")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R approve(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long bianguengId = Long.parseLong(params.get("bianguengId").toString());
        String gonghao = (String) request.getSession().getAttribute("username");
        // 获取教师姓名
        String shenheRen = gonghao;
        JiaoshiEntity jiaoshi = jiaoshiDao.selectById(gonghao);
        if (jiaoshi != null && jiaoshi.getJiaoshixingming() != null) {
            shenheRen = jiaoshi.getJiaoshixingming();
        }
        log.info("审核通过 - 工号: {}, 姓名: {}", gonghao, shenheRen);
        R result = bianguengService.shenheBiangueng(bianguengId, "已通过", shenheRen);

        // 发送审核通过通知给申请人
        try {
            if ((Integer) result.get("code") == 0) {
                JingsaiRenyuanBianguengEntity biangueng2 = bianguengService.selectById(bianguengId);
                if (biangueng2 != null && biangueng2.getCaozuoRenXuehao() != null) {
                    String applicantXuehao = biangueng2.getCaozuoRenXuehao();
                    String bianguengType2 = biangueng2.getBianguengLeixing() != null ? biangueng2.getBianguengLeixing() : "";
                    String tuanduiBianhao2 = biangueng2.getTuanduiBianhao() != null ? biangueng2.getTuanduiBianhao() : "";
                    String title = "人员变更审核通过";
                    String content = String.format("您提交的团队「%s」%s申请已通过审核。", tuanduiBianhao2, bianguengType2);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "人员变更", shenheRen,
                        applicantXuehao, null, "xuesheng",
                        bianguengId, "renyuanbiangueng"
                    );
                    log.info("发送人员变更审核通过通知给申请人: {}, 变更ID: {}", applicantXuehao, bianguengId);
                }
            }
        } catch (Exception e) {
            log.error("发送人员变更审核通过通知异常", e);
        }

        return result;
    }

    @OperationLog("驳回人员变更申请")
    @PostMapping("/shenhe/reject")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R reject(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long bianguengId = Long.parseLong(params.get("bianguengId").toString());
        String gonghao = (String) request.getSession().getAttribute("username");
        // 获取教师姓名
        String shenheRen = gonghao;
        JiaoshiEntity jiaoshi = jiaoshiDao.selectById(gonghao);
        if (jiaoshi != null && jiaoshi.getJiaoshixingming() != null) {
            shenheRen = jiaoshi.getJiaoshixingming();
        }
        log.info("审核驳回 - 工号: {}, 姓名: {}", gonghao, shenheRen);
        R result = bianguengService.shenheBiangueng(bianguengId, "已驳回", shenheRen);

        // 发送审核驳回通知给申请人
        try {
            if ((Integer) result.get("code") == 0) {
                JingsaiRenyuanBianguengEntity biangueng3 = bianguengService.selectById(bianguengId);
                if (biangueng3 != null && biangueng3.getCaozuoRenXuehao() != null) {
                    String applicantXuehao = biangueng3.getCaozuoRenXuehao();
                    String bianguengType3 = biangueng3.getBianguengLeixing() != null ? biangueng3.getBianguengLeixing() : "";
                    String tuanduiBianhao3 = biangueng3.getTuanduiBianhao() != null ? biangueng3.getTuanduiBianhao() : "";
                    String title = "人员变更审核未通过";
                    String content = String.format("您提交的团队「%s」%s申请未通过审核，请联系教师了解详情。", tuanduiBianhao3, bianguengType3);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "人员变更", shenheRen,
                        applicantXuehao, null, "xuesheng",
                        bianguengId, "renyuanbiangueng"
                    );
                    log.info("发送人员变更审核驳回通知给申请人: {}, 变更ID: {}", applicantXuehao, bianguengId);
                }
            }
        } catch (Exception e) {
            log.error("发送人员变更审核驳回通知异常", e);
        }

        return result;
    }

    @IgnoreAuth
    @GetMapping("/history/{tuanduiId}")
    public R history(@PathVariable Long tuanduiId) {
        EntityWrapper<JingsaiRenyuanBianguengEntity> ew = new EntityWrapper<>();
        ew.eq("tuandui_id", tuanduiId);
        ew.orderBy("addtime", false);
        return R.ok().put("data", bianguengService.selectList(ew));
    }

    /**
     * 获取人员变更统计信息
     */
    @GetMapping("/statistics")
    public R getStatistics(HttpServletRequest request) {
        try {
            log.info("========== 人员变更统计数据查询开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            String gonghao = (String) request.getSession().getAttribute("username");
            
            List<Long> jingsaiIds = null;
            
            // 教师只能统计自己创建的竞赛的变更申请
            if ("jiaoshi".equals(tableName)) {
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    jingsaiIds = new java.util.ArrayList<>();
                    for (JingsaixinxiEntity jingsai : myJingsaiList) {
                        jingsaiIds.add(jingsai.getId());
                    }
                } else {
                    Map<String, Object> stats = new java.util.HashMap<>();
                    stats.put("totalBiangueng", 0);
                    stats.put("pendingCount", 0);
                    stats.put("todayProcessed", 0);
                    return R.ok().put("data", stats);
                }
            }
            
            // 总变更数 - selectCount
            EntityWrapper<JingsaiRenyuanBianguengEntity> baseEw = new EntityWrapper<>();
            if (jingsaiIds != null) baseEw.in("jingsai_id", jingsaiIds);
            int totalBiangueng = bianguengService.selectCount(baseEw);
            log.info("变更总数：{}", totalBiangueng);
            
            // 待审核数
            EntityWrapper<JingsaiRenyuanBianguengEntity> pendingEw = new EntityWrapper<>();
            if (jingsaiIds != null) pendingEw.in("jingsai_id", jingsaiIds);
            pendingEw.eq("shenhe_zhuangtai", "待审核");
            int pendingCount = bianguengService.selectCount(pendingEw);
            
            // 已处理数（已通过 + 已驳回）
            EntityWrapper<JingsaiRenyuanBianguengEntity> processedEw = new EntityWrapper<>();
            if (jingsaiIds != null) processedEw.in("jingsai_id", jingsaiIds);
            processedEw.in("shenhe_zhuangtai", "已通过", "已驳回");
            int todayProcessed = bianguengService.selectCount(processedEw);
            log.info("待审核：{}，已处理：{}", pendingCount, todayProcessed);
            
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalBiangueng", totalBiangueng);
            stats.put("pendingCount", pendingCount);
            stats.put("todayProcessed", todayProcessed);
            
            return R.ok().put("data", stats);
        } catch (Exception e) {
            log.error("获取人员变更统计异常", e);
            return R.error("获取统计信息失败");
        }
    }
}
