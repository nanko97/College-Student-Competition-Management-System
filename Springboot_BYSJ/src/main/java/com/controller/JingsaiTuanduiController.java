package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiTuanduiChengyuanEntity;
import com.entity.JingsaiTuanduiEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiTuanduiChengyuanService;
import com.service.JingsaiTuanduiService;
import com.service.JingsaixinxiService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jingsai/tuandui")
@Slf4j
public class JingsaiTuanduiController {
    @Autowired private JingsaiTuanduiService tuanduiService;
    @Autowired private JingsaiTuanduiChengyuanService chengyuanService;
    @Autowired private JingsaixinxiService jingsaixinxiService;

    @OperationLog("创建竞赛团队")
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public R create(@RequestBody JingsaiTuanduiEntity tuandui, HttpServletRequest request) {
        String caozuoRen = (String) request.getSession().getAttribute("username");
        return tuanduiService.createTuandui(tuandui, caozuoRen);
    }

    @OperationLog("更新竞赛团队")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaiTuanduiEntity tuandui) {
        tuanduiService.updateById(tuandui);
        return R.ok("更新成功");
    }

    @OperationLog("删除竞赛团队")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        tuanduiService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok("删除成功");
    }

    /**
     * 查询所有团队列表（学生申请加入时使用）
     * 允许所有登录用户访问
     */
    @IgnoreAuth
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        log.info("查询团队列表 - 参数:{}", params);
        
        // 获取当前用户信息
        String username = (String) request.getSession().getAttribute("username");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        log.info("查询团队列表 - 用户名:{}, 角色:{}", username, tableName);
        
        // 所有用户都可以查看所有已通过的团队（用于学生申请加入）
        EntityWrapper<JingsaiTuanduiEntity> ew = new EntityWrapper<>();
        ew.eq("shenhe_zhuangtai", "已通过");  // 只显示审核通过的团队
        
        // 处理搜索条件
        String tuanduiMingcheng = (String) params.get("tuanduiMingcheng");
        if (tuanduiMingcheng != null && !tuanduiMingcheng.isEmpty()) {
            ew.like("tuandui_mingcheng", tuanduiMingcheng);
        }
        
        String jingsaimingcheng = (String) params.get("jingsaimingcheng");
        if (jingsaimingcheng != null && !jingsaimingcheng.isEmpty()) {
            ew.like("jingsaimingcheng", jingsaimingcheng);
        }
        
        PageUtils page = tuanduiService.queryPage(params, ew);
        log.info("返回团队数量: {}", page.getList().size());
        return R.ok().put("page", page);
    }

    /**
     * 查询所有团队列表（教师/管理员审核时使用）
     * 教师只能查看自己创建的竞赛的团队
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 获取当前用户信息
        String username = (String) request.getSession().getAttribute("username");
        String tableName = (String) request.getSession().getAttribute("tableName");
        String role = (String) request.getSession().getAttribute("role");
        
        log.info("查询团队列表 - 用户名:{}, 角色:{}", username, role);
        
        EntityWrapper<JingsaiTuanduiEntity> ew = new EntityWrapper<>();
        
        // 【重要】教师只能查看自己创建的竞赛的团队，管理员可以查看所有
        if ("教师".equals(role)) {
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", username);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            log.info("教师 {} 查询到 {} 个竞赛", username, myJingsaiList != null ? myJingsaiList.size() : 0);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛ID列表
                List<Long> jingsaiIds = new java.util.ArrayList<>();
                for (JingsaixinxiEntity jingsai : myJingsaiList) {
                    jingsaiIds.add(jingsai.getId());
                }
                // 只查询这些竞赛的团队
                ew.in("jingsai_id", jingsaiIds);
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的团队，竞赛IDs: {}", username, jingsaiIds.size(), jingsaiIds);
            } else {
                // 如果教师没有创建任何竞赛，返回空列表
                log.info("教师 {} 没有创建任何竞赛，返回空列表", username);
                return R.ok().put("page", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1));
            }
        }
        // 管理员不添加限制，可以查看所有团队
        
        PageUtils page = tuanduiService.queryPage(params, ew);
        log.info("返回团队数量: {}", page.getList().size());
        return R.ok().put("page", page);
    }

    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().put("jingsaituandui", tuanduiService.selectById(id));
    }

    @OperationLog("审核竞赛团队")
    @PostMapping("/shenhe")
    @Transactional(rollbackFor = Exception.class)
    public R shenhe(@RequestBody Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        String shenheZhuangtai = params.get("shenheZhuangtai").toString();
        String shenheYijian = params.get("shenheYijian") != null ? params.get("shenheYijian").toString() : "";
        JingsaiTuanduiEntity tuandui = tuanduiService.selectById(id);
        if (tuandui != null) {
            tuandui.setShenheZhuangtai(shenheZhuangtai);
            tuandui.setShenheYijian(shenheYijian);
            tuanduiService.updateById(tuandui);
            return R.ok("审核成功");
        }
        return R.error("团队不存在");
    }

    /**
     * 查询我参与的团队列表
     */
    @GetMapping("/my/list")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        log.info("查询团队列表 - 用户名:{}, 角色:{}", username, tableName);
        
        // 教师/管理员可以查看所有团队
        if ("jiaoshi".equals(tableName) || "users".equals(tableName)) {
            PageUtils page = tuanduiService.queryPage(params);
            log.info("教师查询所有团队，返回:{} 条", page.getList().size());
            return R.ok().put("page", page);
        }
        
        // 学生查询自己参与的团队
        String xuehao = username;
        log.info("学生查询参与的团队，学号:{}", xuehao);
        
        // 先查询该学生参与的所有团队ID
        EntityWrapper<JingsaiTuanduiChengyuanEntity> memberEw = new EntityWrapper<>();
        memberEw.eq("xuehao", xuehao);
        memberEw.eq("is_active", "是");
        List<JingsaiTuanduiChengyuanEntity> myMemberships = chengyuanService.selectList(memberEw);
        
        log.info("学生参与的团队数量:{}", myMemberships.size());
        
        if (myMemberships.isEmpty()) {
            // 如果没有参与任何团队，只查询作为负责人的团队
            EntityWrapper<JingsaiTuanduiEntity> ew = new EntityWrapper<>();
            ew.eq("fuzeren_xuehao", xuehao);
            PageUtils page = tuanduiService.queryPage(params, ew);
            log.info("作为负责人的团队数量:{}", page.getList().size());
            return R.ok().put("page", page);
        } else {
            // 提取团队ID列表
            java.util.List<Long> tuanduiIds = new java.util.ArrayList<>();
            for (JingsaiTuanduiChengyuanEntity member : myMemberships) {
                tuanduiIds.add(member.getTuanduiId());
            }
            
            // 查询这些团队的详细信息
            EntityWrapper<JingsaiTuanduiEntity> ew = new EntityWrapper<>();
            ew.in("id", tuanduiIds);
            PageUtils page = tuanduiService.queryPage(params, ew);
            log.info("查询到的团队数量:{}", page.getList().size());
            return R.ok().put("page", page);
        }
    }

    @OperationLog("添加团队成员")
    @PostMapping("/chengyuan/add")
    @Transactional(rollbackFor = Exception.class)
    public R addChengyuan(@RequestBody JingsaiTuanduiChengyuanEntity chengyuan) {
        return chengyuanService.addChengyuan(chengyuan);
    }

    @OperationLog("移除团队成员")
    @PostMapping("/chengyuan/remove")
    @Transactional(rollbackFor = Exception.class)
    public R removeChengyuan(@RequestBody Map<String, Object> params) {
        Long chengyuanId = Long.parseLong(params.get("chengyuanId").toString());
        JingsaiTuanduiChengyuanEntity chengyuan = chengyuanService.selectById(chengyuanId);
        if (chengyuan != null) {
            chengyuan.setIsActive("否");
            chengyuanService.updateById(chengyuan);
            // 更新团队人数
            JingsaiTuanduiEntity tuandui = tuanduiService.selectById(chengyuan.getTuanduiId());
            if (tuandui != null && tuandui.getChengyuanRenshu() > 1) {
                tuandui.setChengyuanRenshu(tuandui.getChengyuanRenshu() - 1);
                tuanduiService.updateById(tuandui);
            }
        }
        return R.ok("移除成功");
    }

    /**
     * 解散团队（仅负责人可操作）
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/dissolve")
    public R dissolveTeam(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long teamId = Long.parseLong(params.get("id").toString());
        String username = (String) request.getSession().getAttribute("username");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        log.info("解散团队请求 - 团队ID:{}, 用户名:{}, 角色:{}", teamId, username, tableName);
        
        // 验证权限：只有负责人可以解散团队
        JingsaiTuanduiEntity tuandui = tuanduiService.selectById(teamId);
        if (tuandui == null) {
            return R.error("团队不存在");
        }
        
        if (!tuandui.getFuzerenXuehao().equals(username)) {
            return R.error("只有团队负责人可以解散团队");
        }
        
        try {
            // 1. 删除所有团队成员记录（物理删除）
            EntityWrapper<JingsaiTuanduiChengyuanEntity> memberEw = new EntityWrapper<>();
            memberEw.eq("tuandui_id", teamId);
            chengyuanService.delete(memberEw);
            log.info("已删除团队成员记录");
            
            // 2. 删除团队（物理删除）
            tuanduiService.deleteById(teamId);
            log.info("已删除团队记录");
            
            log.info("团队解散成功 - 团队ID:{}, 团队名称:{}", teamId, tuandui.getTuanduiMingcheng());
            return R.ok("团队已解散");
        } catch (Exception e) {
            log.error("解散团队失败:", e);
            return R.error("解散团队失败：" + e.getMessage());
        }
    }

    @IgnoreAuth
    @GetMapping("/chengyuan/list/{tuanduiId}")
    public R chengyuanList(@PathVariable Long tuanduiId) {
        log.info("查询团队成员列表(路径参数) - 团队ID: {}", tuanduiId);
        
        EntityWrapper<JingsaiTuanduiChengyuanEntity> ew = new EntityWrapper<>();
        ew.eq("tuandui_id", tuanduiId);
        ew.eq("is_active", "是");
        ew.orderBy("juese", false); // 负责人排在前面
        ew.orderBy("ruzui_shijian", true);
        
        List<JingsaiTuanduiChengyuanEntity> list = chengyuanService.selectList(ew);
        log.info("查询到团队成员数量: {}", list.size());
        
        return R.ok().put("data", list);
    }

    // 兼容前端 /jingsai/tuanduichengyuan/list?tuanduiId=xxx 路径
    @IgnoreAuth
    @GetMapping("/tuanduichengyuan/list")
    public R tuanduiChengyuanList(@RequestParam Map<String, Object> params) {
        Long tuanduiId = Long.parseLong(params.get("tuanduiId").toString());
        log.info("查询团队成员列表 - 团队ID: {}", tuanduiId);
        
        EntityWrapper<JingsaiTuanduiChengyuanEntity> ew = new EntityWrapper<>();
        ew.eq("tuandui_id", tuanduiId);
        ew.eq("is_active", "是");
        ew.orderBy("juese", false); // 负责人排在前面
        ew.orderBy("ruzui_shijian", true);
        
        List<JingsaiTuanduiChengyuanEntity> list = chengyuanService.selectList(ew);
        log.info("查询到团队成员数量: {}", list.size());
        
        return R.ok().put("data", list);
    }

    /**
     * 检查学生的团队状态
     */
    @GetMapping("/myStatus")
    public R myStatus(@RequestParam String xuehao) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        // 检查是否是负责人
        EntityWrapper<JingsaiTuanduiEntity> leaderEw = new EntityWrapper<>();
        leaderEw.eq("fuzeren_xuehao", xuehao);
        List<JingsaiTuanduiEntity> leaderTeams = tuanduiService.selectList(leaderEw);
        
        if (!leaderTeams.isEmpty()) {
            result.put("isLeader", true);
            result.put("teamId", leaderTeams.get(0).getId());
            result.put("hasTeam", true);
            return R.ok().put("data", result);
        }
        
        // 检查是否是普通成员
        EntityWrapper<JingsaiTuanduiChengyuanEntity> memberEw = new EntityWrapper<>();
        memberEw.eq("xuehao", xuehao);
        memberEw.eq("is_active", "是");
        List<JingsaiTuanduiChengyuanEntity> memberships = chengyuanService.selectList(memberEw);
        
        if (!memberships.isEmpty()) {
            result.put("isLeader", false);
            result.put("teamId", memberships.get(0).getTuanduiId());
            result.put("hasTeam", true);
        } else {
            result.put("isLeader", false);
            result.put("teamId", null);
            result.put("hasTeam", false);
        }
        
        return R.ok().put("data", result);
    }

    /**
     * 申请加入团队（已迁移到 TuanduiApplyController）
     * @deprecated 请使用 /tuandui/apply/join
     */
    @Deprecated
    @PostMapping("/applyJoin")
    public R applyJoin(@RequestBody Map<String, Object> params) {
        return R.error("请使用新的接口 /tuandui/apply/join");
    }

    /**
     * 申请退出团队（已迁移到 TuanduiApplyController）
     * @deprecated 请使用 /tuandui/apply/quit
     */
    @Deprecated
    @PostMapping("/applyQuit")
    public R applyQuit(@RequestBody Map<String, Object> params) {
        return R.error("请使用新的接口 /tuandui/apply/quit");
    }
}
