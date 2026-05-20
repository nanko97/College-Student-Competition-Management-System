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
        return bianguengService.applyBiangueng(biangueng, caozuoRen);
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
        ew.eq("shenhe_zhuangtai", "审核中");
        
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
        return bianguengService.shenheBiangueng(bianguengId, "已通过", shenheRen);
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
        return bianguengService.shenheBiangueng(bianguengId, "已驳回", shenheRen);
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
            String role = (String) request.getSession().getAttribute("role");
            String gonghao = (String) request.getSession().getAttribute("username");
            
            // 使用更可靠的查询方式：先查询所有，再过滤
            List<JingsaiRenyuanBianguengEntity> allBiangueng = bianguengService.selectList(null);
            
            // 教师只能统计自己创建的竞赛的变更申请
            if ("教师".equals(role)) {
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    List<Long> jingsaiIds = new java.util.ArrayList<>();
                    for (JingsaixinxiEntity jingsai : myJingsaiList) {
                        jingsaiIds.add(jingsai.getId());
                    }
                    final List<Long> finalJingsaiIds = jingsaiIds;
                    allBiangueng = allBiangueng.stream()
                        .filter(b -> finalJingsaiIds.contains(b.getJingsaiId()))
                        .collect(java.util.stream.Collectors.toList());
                } else {
                    Map<String, Object> stats = new java.util.HashMap<>();
                    stats.put("totalBiangueng", 0);
                    stats.put("pendingCount", 0);
                    stats.put("todayProcessed", 0);
                    return R.ok().put("data", stats);
                }
            }
            
            // 总变更数
            int totalBiangueng = allBiangueng != null ? allBiangueng.size() : 0;
            log.info("变更总数：{}", totalBiangueng);
            
            // 待审核数和已处理数
            int pendingCount = 0;
            int todayProcessed = 0;
            if (allBiangueng != null) {
                for (JingsaiRenyuanBianguengEntity biangueng : allBiangueng) {
                    String status = biangueng.getShenheZhuangtai();
                    if ("审核中".equals(status)) {
                        pendingCount++;
                    } else if ("已通过".equals(status) || "已驳回".equals(status)) {
                        todayProcessed++;
                    }
                }
            }
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
