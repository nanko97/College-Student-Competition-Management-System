package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dao.JiaoshiDao;
import com.entity.JiaoshiEntity;
import com.entity.JingsaiJibieBanbenEntity;
import com.entity.JingsaiJinjiGuanxiEntity;
import com.entity.JingsaiJinjiJiluEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiJibieBanbenService;
import com.service.JingsaiJinjiGuanxiService;
import com.service.JingsaiJinjiJiluService;
import com.service.JingsaixinxiService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 竞赛晋级管理控制器
 * 功能：配置晋级关系、发起晋级、审核晋级、查询晋级记录
 */
@RestController
@RequestMapping("/jingsai/jinji")
@Slf4j
public class JingsaiJinjiController {

    @Autowired
    private JingsaiJinjiGuanxiService jinjiGuanxiService;

    @Autowired
    private JingsaiJinjiJiluService jinjiJiluService;

    @Autowired
    private JingsaixinxiService jingsaixinxiService;

    @Autowired
    private JingsaiJibieBanbenService jibieBanbenService;

    @Autowired
    private JiaoshiDao jiaoshiDao;

    @Autowired
    private com.service.XiaoxiTongzhiService xiaoxiTongzhiService;

    /**
     * 配置晋级关系
     */
    @OperationLog("配置竞赛晋级关系")
    @PostMapping("/guanxi/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R saveGuanxi(@RequestBody JingsaiJinjiGuanxiEntity guanxi, HttpServletRequest request) {
        try {
            // 联动1：验证父竞赛和子竞赛不能相同
            if (guanxi.getFuJingsaiId() != null && guanxi.getZiJingsaiId() != null
                && guanxi.getFuJingsaiId().equals(guanxi.getZiJingsaiId())) {
                return R.error("父竞赛和子竞赛不能相同");
            }

            // 联动2：自动填充竞赛名称（非DB字段，仅用于前端展示）
            if (guanxi.getFuJingsaiId() != null) {
                JingsaixinxiEntity fuJingsai = jingsaixinxiService.selectById(guanxi.getFuJingsaiId());
                if (fuJingsai != null) {
                    guanxi.setFuJingsaimingcheng(fuJingsai.getJingsaimingcheng());
                }
            }
            if (guanxi.getZiJingsaiId() != null) {
                JingsaixinxiEntity ziJingsai = jingsaixinxiService.selectById(guanxi.getZiJingsaiId());
                if (ziJingsai != null) {
                    guanxi.setZiJingsaimingcheng(ziJingsai.getJingsaimingcheng());
                }
            }

            // 联动3：自动填充父级别（从当前版本）
            if (guanxi.getFuJingsaiId() != null && !StringUtils.hasText(guanxi.getFuJibie())) {
                EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
                ew.eq("jingsai_id", guanxi.getFuJingsaiId());
                ew.eq("is_current", "是");
                JingsaiJibieBanbenEntity currentBanben = jibieBanbenService.selectOne(ew);
                if (currentBanben != null && StringUtils.hasText(currentBanben.getJibie())) {
                    guanxi.setFuJibie(currentBanben.getJibie());
                    log.info("联动：自动填充父级别为 {}，竞赛ID: {}", currentBanben.getJibie(), guanxi.getFuJingsaiId());
                } else {
                    // 如果没有当前版本，尝试从竞赛信息获取
                    JingsaixinxiEntity fuJingsai = jingsaixinxiService.selectById(guanxi.getFuJingsaiId());
                    if (fuJingsai != null && StringUtils.hasText(fuJingsai.getJingsaiJibie())) {
                        guanxi.setFuJibie(fuJingsai.getJingsaiJibie());
                        log.info("联动：从竞赛信息自动填充父级别为 {}", fuJingsai.getJingsaiJibie());
                    }
                }
            }

            // 联动4：自动填充子级别（从当前版本）
            if (guanxi.getZiJingsaiId() != null && !StringUtils.hasText(guanxi.getZiJibie())) {
                EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
                ew.eq("jingsai_id", guanxi.getZiJingsaiId());
                ew.eq("is_current", "是");
                JingsaiJibieBanbenEntity currentBanben = jibieBanbenService.selectOne(ew);
                if (currentBanben != null && StringUtils.hasText(currentBanben.getJibie())) {
                    guanxi.setZiJibie(currentBanben.getJibie());
                    log.info("联动：自动填充子级别为 {}，竞赛ID: {}", currentBanben.getJibie(), guanxi.getZiJingsaiId());
                } else {
                    JingsaixinxiEntity ziJingsai = jingsaixinxiService.selectById(guanxi.getZiJingsaiId());
                    if (ziJingsai != null && StringUtils.hasText(ziJingsai.getJingsaiJibie())) {
                        guanxi.setZiJibie(ziJingsai.getJingsaiJibie());
                        log.info("联动：从竞赛信息自动填充子级别为 {}", ziJingsai.getJingsaiJibie());
                    }
                }
            }

            // 检查是否已存在
            JingsaiJinjiGuanxiEntity exist = jinjiGuanxiService.getByFuZiJingsai(
                guanxi.getFuJingsaiId(), guanxi.getZiJingsaiId());

            StringBuilder msg = new StringBuilder();
            if (exist != null) {
                // 更新现有配置
                String oldFuJibie = exist.getFuJibie();
                String oldZiJibie = exist.getZiJibie();
                exist.setFuJibie(guanxi.getFuJibie());
                exist.setZiJibie(guanxi.getZiJibie());
                exist.setJinjiType(guanxi.getJinjiType());
                exist.setJinjiRule(guanxi.getJinjiRule());
                exist.setZuidiFenshu(guanxi.getZuidiFenshu());
                exist.setZuidiMingci(guanxi.getZuidiMingci());
                exist.setIsActive(guanxi.getIsActive());
                jinjiGuanxiService.updateById(exist);
                msg.append("晋级关系更新成功");
                // 联动提示：级别变更信息
                if (!guanxi.getFuJibie().equals(oldFuJibie) || !guanxi.getZiJibie().equals(oldZiJibie)) {
                    msg.append("，级别已同步更新");
                }
                return R.ok(msg.toString());
            } else {
                // 新增配置
                guanxi.setId(IdWorker.getId());
                jinjiGuanxiService.insert(guanxi);
                msg.append("晋级关系配置成功");
                // 联动提示
                if (StringUtils.hasText(guanxi.getFuJibie()) || StringUtils.hasText(guanxi.getZiJibie())) {
                    msg.append("，级别已自动填充");
                }
                return R.ok(msg.toString());
            }
        } catch (Exception e) {
            log.error("配置晋级关系异常", e);
            return R.error("配置失败，请重试");
        }
    }

    /**
     * 查询晋级关系列表
     * 教师只能查看自己组织的竞赛的晋级关系
     */
    @GetMapping("/guanxi/list")
    public R listGuanxi(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 权限控制：根据用户角色过滤数据
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("当前用户角色tableName：{}", tableName);
        
        // 构建查询条件
        EntityWrapper<JingsaiJinjiGuanxiEntity> ew = new EntityWrapper<>();
        
        // 教师只能查看自己组织的竞赛的晋级关系
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询自己组织的竞赛的晋级关系", gonghao);
            
            // 先查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛 ID 列表
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(java.util.stream.Collectors.toList());
                
                // 晋级关系的fu_jingsai_id或zi_jingsai_id必须在这个教师的竞赛ID列表中
                ew.andNew().in("fu_jingsai_id", myJingsaiIds).or().in("zi_jingsai_id", myJingsaiIds);
                log.info("教师 {} 查询到 {} 个竞赛的晋级关系", gonghao, myJingsaiIds.size());
            } else {
                // 该教师没有创建任何竞赛，返回空结果
                ew.eq("id", -1);
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
            }
        }
        
        // 处理父竞赛筛选
        if (params.get("fuJingsaiId") != null && !params.get("fuJingsaiId").toString().isEmpty()) {
            ew.eq("fu_jingsai_id", params.get("fuJingsaiId"));
            log.debug("晋级关系筛选 - 父竞赛ID: {}", params.get("fuJingsaiId"));
        }
        
        // 处理子竞赛筛选
        if (params.get("ziJingsaiId") != null && !params.get("ziJingsaiId").toString().isEmpty()) {
            ew.eq("zi_jingsai_id", params.get("ziJingsaiId"));
            log.debug("晋级关系筛选 - 子竞赛ID: {}", params.get("ziJingsaiId"));
        }
        
        PageUtils page = jinjiGuanxiService.queryPage(params, ew);
        
        // 为每条记录添加竞赛名称
        if (page != null && page.getList() != null) {
            for (Object obj : page.getList()) {
                JingsaiJinjiGuanxiEntity entity = (JingsaiJinjiGuanxiEntity) obj;
                
                // 查询父竞赛名称
                if (entity.getFuJingsaiId() != null) {
                    JingsaixinxiEntity fuJingsai = jingsaixinxiService.selectById(entity.getFuJingsaiId());
                    if (fuJingsai != null) {
                        entity.setFuJingsaimingcheng(fuJingsai.getJingsaimingcheng());
                    }
                }
                
                // 查询子竞赛名称
                if (entity.getZiJingsaiId() != null) {
                    JingsaixinxiEntity ziJingsai = jingsaixinxiService.selectById(entity.getZiJingsaiId());
                    if (ziJingsai != null) {
                        entity.setZiJingsaimingcheng(ziJingsai.getJingsaimingcheng());
                    }
                }
            }
        }
        
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 发起晋级申请
     */
    @OperationLog("发起竞赛晋级申请")
    @PostMapping("/initiate")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R initiateJinji(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long baomingId = Long.parseLong(params.get("baomingId").toString());
            Long xinJingsaiId = Long.parseLong(params.get("xinJingsaiId").toString());
            String jinjiYuanyin = params.get("jinjiYuanyin") != null ? params.get("jinjiYuanyin").toString() : "";
            String caozuoRen = (String) request.getSession().getAttribute("username");

            R result = jinjiJiluService.initiateJinji(baomingId, xinJingsaiId, jinjiYuanyin, caozuoRen);
            return result;
        } catch (Exception e) {
            log.error("发起晋级异常", e);
            return R.error("操作失败，请重试");
        }
    }

    /**
     * 学生申请晋级（简化版）
     */
    @OperationLog("学生申请竞赛晋级")
    @PostMapping("/apply")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R applyJinji(HttpServletRequest request) {
        try {
            String xuehao = (String) request.getSession().getAttribute("username");
            log.info("学生申请晋级 - 学号: {}", xuehao);
            
            // TODO: 这里需要根据实际业务逻辑实现
            // 目前返回成功消息，实际应该根据学生的报名记录和晋级规则自动生成晋级申请
            
            return R.ok("晋级申请已提交，等待审核");
        } catch (Exception e) {
            log.error("学生申请晋级异常", e);
            return R.error("申请失败，请重试");
        }
    }

    /**
     * 批量发起晋级
     */
    @OperationLog("批量发起竞赛晋级")
    @PostMapping("/batchInitiate")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R batchInitiateJinji(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> baomingIds = (List<Long>) params.get("baomingIds");
            Long xinJingsaiId = Long.parseLong(params.get("xinJingsaiId").toString());
            String jinjiYuanyin = params.get("jinjiYuanyin") != null ? params.get("jinjiYuanyin").toString() : "";
            String caozuoRen = (String) request.getSession().getAttribute("username");

            R result = jinjiJiluService.batchInitiateJinji(baomingIds, xinJingsaiId, jinjiYuanyin, caozuoRen);
            return result;
        } catch (Exception e) {
            log.error("批量晋级异常", e);
            return R.error("操作失败，请重试");
        }
    }

    /**
     * 查询我的晋级记录
     */
    @GetMapping("/my/list")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String xuehao = (String) request.getSession().getAttribute("username");
        log.info("查询我的晋级记录 - 学号: {}", xuehao);
        
        // 构建查询条件
        EntityWrapper<JingsaiJinjiJiluEntity> ew = new EntityWrapper<>();
        ew.eq("xuehao", xuehao);
        
        // 添加竞赛名称筛选
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("yuan_jingsaimingcheng", params.get("jingsaimingcheng").toString());
        }
        
        // 添加审核状态筛选
        if (params.get("jinjiZhuangtai") != null && !params.get("jinjiZhuangtai").toString().isEmpty()) {
            ew.eq("jinji_zhuangtai", params.get("jinjiZhuangtai").toString());
        }
        
        PageUtils page = jinjiJiluService.queryPage(params, ew);
        log.info("查询结果 - 总数: {}, 列表大小: {}", page.getTotal(), page.getList().size());
        return R.ok().put("page", page);
    }

    /**
     * 分页查询待审核晋级（教师/管理员）
     * 教师只能审核自己创建的竞赛的晋级申请
     */
    @GetMapping("/shenhe/page")
    public R shenhePage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 获取当前用户信息
        String gonghao = (String) request.getSession().getAttribute("username");
        String role = (String) request.getSession().getAttribute("role");
        
        log.info("查询待审核晋级记录 - 工号: {}, 角色: {}", gonghao, role);
        
        EntityWrapper<JingsaiJinjiJiluEntity> ew = new EntityWrapper<>();
        ew.eq("jinji_zhuangtai", "待审核");
        
        // 【重要】教师只能查看自己创建的竞赛的晋级申请，管理员可以查看所有
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
                // 只查询这些竞赛的晋级申请（原竞赛或目标竞赛都属于该教师）
                ew.andNew().in("yuan_jingsai_id", jingsaiIds).or().in("xin_jingsai_id", jingsaiIds);
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的晋级申请，竞赛IDs: {}", gonghao, jingsaiIds.size(), jingsaiIds);
            } else {
                // 如果教师没有创建任何竞赛，返回空列表
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                return R.ok().put("data", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1))
                        .put("page", new PageUtils(new java.util.ArrayList<>(), 0, 10, 1));
            }
        }
        // 管理员不添加限制，可以查看所有申请
        
        PageUtils page = jinjiJiluService.queryPage(params, ew);
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 审核通过晋级
     */
    @OperationLog("通过竞赛晋级申请")
    @PostMapping("/shenhe/approve")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R approveJinji(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long jinjiId = Long.parseLong(params.get("jinjiId").toString());
            String gonghao = (String) request.getSession().getAttribute("username");
            // 获取教师姓名
            String shenheRen = gonghao;
            JiaoshiEntity jiaoshi = jiaoshiDao.selectById(gonghao);
            if (jiaoshi != null && jiaoshi.getJiaoshixingming() != null) {
                shenheRen = jiaoshi.getJiaoshixingming();
            }
            log.info("审核通过晋级 - 工号: {}, 姓名: {}", gonghao, shenheRen);

            R result = jinjiJiluService.shenheJinji(jinjiId, "已通过", shenheRen);

            // 发送晋级审核通过通知给学生
            try {
                JingsaiJinjiJiluEntity jinji = jinjiJiluService.selectById(jinjiId);
                if (jinji != null && jinji.getXuehao() != null) {
                    String studentXuehao = jinji.getXuehao();
                    String yuanJingsaiName = jinji.getYuanJingsaimingcheng() != null ? jinji.getYuanJingsaimingcheng() : "";
                    String xinJingsaiName = jinji.getXinJingsaimingcheng() != null ? jinji.getXinJingsaimingcheng() : "";
                    String title = "晋级审核通过";
                    String content = String.format("您从「%s」晋级到「%s」的申请已通过审核。", yuanJingsaiName, xinJingsaiName);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "晋级结果", shenheRen,
                        studentXuehao, null, "xuesheng",
                        jinjiId, "jinji"
                    );
                    log.info("发送晋级审核通过通知给学生: {}, 晋级ID: {}", studentXuehao, jinjiId);
                }
            } catch (Exception e) {
                log.error("发送晋级审核通知异常", e);
            }

            return result;
        } catch (Exception e) {
            log.error("审核晋级异常", e);
            return R.error("审核失败，请重试");
        }
    }

    /**
     * 审核驳回晋级
     */
    @OperationLog("驳回竞赛晋级申请")
    @PostMapping("/shenhe/reject")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R rejectJinji(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long jinjiId = Long.parseLong(params.get("jinjiId").toString());
            String gonghao = (String) request.getSession().getAttribute("username");
            // 获取教师姓名
            String shenheRen = gonghao;
            JiaoshiEntity jiaoshi = jiaoshiDao.selectById(gonghao);
            if (jiaoshi != null && jiaoshi.getJiaoshixingming() != null) {
                shenheRen = jiaoshi.getJiaoshixingming();
            }
            log.info("审核驳回晋级 - 工号: {}, 姓名: {}", gonghao, shenheRen);

            R result = jinjiJiluService.shenheJinji(jinjiId, "已驳回", shenheRen);

            // 发送晋级审核驳回通知给学生
            try {
                JingsaiJinjiJiluEntity jinji = jinjiJiluService.selectById(jinjiId);
                if (jinji != null && jinji.getXuehao() != null) {
                    String studentXuehao = jinji.getXuehao();
                    String yuanJingsaiName = jinji.getYuanJingsaimingcheng() != null ? jinji.getYuanJingsaimingcheng() : "";
                    String title = "晋级审核未通过";
                    String content = String.format("您从「%s」的晋级申请未通过审核，请联系教师了解详情。", yuanJingsaiName);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "晋级结果", shenheRen,
                        studentXuehao, null, "xuesheng",
                        jinjiId, "jinji"
                    );
                    log.info("发送晋级审核驳回通知给学生: {}, 晋级ID: {}", studentXuehao, jinjiId);
                }
            } catch (Exception e) {
                log.error("发送晋级驳回通知异常", e);
            }

            return result;
        } catch (Exception e) {
            log.error("驳回晋级异常", e);
            return R.error("操作失败，请重试");
        }
    }

    /**
     * 删除晋级关系
     * 联动：删除成功后提示删除数量
     */
    @OperationLog("删除竞赛晋级关系")
    @PostMapping("/guanxi/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R deleteGuanxi(@RequestBody Long[] ids) {
        try {
            if (ids == null || ids.length == 0) {
                return R.error("请选择要删除的晋级关系");
            }
            jinjiGuanxiService.deleteBatchIds(Arrays.asList(ids));
            log.info("删除晋级关系 {} 条，IDs: {}", ids.length, Arrays.toString(ids));
            return R.ok("删除成功(" + ids.length + "条晋级关系)");
        } catch (Exception e) {
            log.error("删除晋级关系异常", e);
            return R.error("删除失败，请重试");
        }
    }

    /**
     * 查询竞赛晋级树
     */
    @IgnoreAuth
    @GetMapping("/tree/{jingsaiId}")
    public R getJinjiTree(@PathVariable Long jingsaiId) {
        // 查询该竞赛的所有晋级关系（作为父级）
        EntityWrapper<JingsaiJinjiGuanxiEntity> ewFu = new EntityWrapper<>();
        ewFu.eq("fu_jingsai_id", jingsaiId);
        ewFu.eq("is_active", "是");
        List<JingsaiJinjiGuanxiEntity> fuList = jinjiGuanxiService.selectList(ewFu);

        // 查询该竞赛的所有晋级关系（作为子级）
        EntityWrapper<JingsaiJinjiGuanxiEntity> ewZi = new EntityWrapper<>();
        ewZi.eq("zi_jingsai_id", jingsaiId);
        ewZi.eq("is_active", "是");
        List<JingsaiJinjiGuanxiEntity> ziList = jinjiGuanxiService.selectList(ewZi);

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("asFu", fuList);
        data.put("asZi", ziList);

        return R.ok().put("data", data);
    }

    /**
     * 获取晋级统计信息
     * 教师只能查看自己创建的竞赛相关的晋级数据
     * 学生只能查看自己的晋级数据
     */
    @GetMapping("/statistics")
    public R getStatistics(HttpServletRequest request) {
        try {
            log.info("========== 晋级统计数据查询开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.info("当前用户角色tableName：{}", tableName);
            
            // 获取用户相关的竞赛ID列表
            final List<Long> myJingsaiIds = new java.util.ArrayList<>();
            
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                log.info("教师角色，工号：{}", gonghao);
                
                // 查询该教师创建的所有竞赛ID
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    for (JingsaixinxiEntity jingsai : myJingsaiList) {
                        myJingsaiIds.add(jingsai.getId());
                    }
                    log.info("教师 {} 创建了 {} 个竞赛，IDs: {}", gonghao, myJingsaiIds.size(), myJingsaiIds);
                } else {
                    log.info("教师 {} 还没有创建任何竞赛", gonghao);
                }
            } else if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                log.info("学生角色，学号：{}", xuehao);
                
                // 学生通过xuehao字段过滤自己的晋级记录，不需要竞赛ID列表
            }
            
            // 使用更可靠的查询方式：先查询所有，再过滤
            List<JingsaiJinjiGuanxiEntity> allGuanxi = jinjiGuanxiService.selectList(null);
            List<JingsaiJinjiJiluEntity> allJilu = jinjiJiluService.selectList(null);
            
            // 根据角色过滤数据
            List<JingsaiJinjiGuanxiEntity> filteredGuanxi = allGuanxi;
            List<JingsaiJinjiJiluEntity> filteredJilu = allJilu;
            
            if ("jiaoshi".equals(tableName)) {
                // 教师：只查看自己竞赛相关的晋级关系和记录
                if (!myJingsaiIds.isEmpty()) {
                    filteredGuanxi = new java.util.ArrayList<>();
                    if (allGuanxi != null) {
                        for (JingsaiJinjiGuanxiEntity guanxi : allGuanxi) {
                            if (myJingsaiIds.contains(guanxi.getFuJingsaiId()) || 
                                myJingsaiIds.contains(guanxi.getZiJingsaiId())) {
                                filteredGuanxi.add(guanxi);
                            }
                        }
                    }
                    
                    filteredJilu = new java.util.ArrayList<>();
                    if (allJilu != null) {
                        for (JingsaiJinjiJiluEntity jilu : allJilu) {
                            if (myJingsaiIds.contains(jilu.getYuanJingsaiId()) || 
                                myJingsaiIds.contains(jilu.getXinJingsaiId())) {
                                filteredJilu.add(jilu);
                            }
                        }
                    }
                } else {
                    filteredGuanxi = new java.util.ArrayList<>();
                    filteredJilu = new java.util.ArrayList<>();
                }
            } else if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                // 学生：只查看自己的晋级记录
                filteredGuanxi = new java.util.ArrayList<>(); // 学生不看晋级关系配置
                filteredJilu = new java.util.ArrayList<>();
                if (allJilu != null) {
                    for (JingsaiJinjiJiluEntity jilu : allJilu) {
                        if (xuehao.equals(jilu.getXuehao())) {
                            filteredJilu.add(jilu);
                        }
                    }
                }
            }
            
            // 总晋级关系数
            int totalGuanxi = filteredGuanxi != null ? filteredGuanxi.size() : 0;
            log.info("晋级关系总数：{}", totalGuanxi);
            
            // 活跃晋级关系数
            int activeGuanxi = 0;
            if (filteredGuanxi != null) {
                for (JingsaiJinjiGuanxiEntity guanxi : filteredGuanxi) {
                    if ("是".equals(guanxi.getIsActive())) {
                        activeGuanxi++;
                    }
                }
            }
            log.info("活跃晋级关系数：{}", activeGuanxi);
            
            // 总晋级记录数
            int totalJilu = filteredJilu != null ? filteredJilu.size() : 0;
            log.info("晋级记录总数：{}", totalJilu);
            
            // 待审核、已通过、已驳回数
            int pendingCount = 0;
            int approvedCount = 0;
            int rejectedCount = 0;
            if (filteredJilu != null) {
                for (JingsaiJinjiJiluEntity jilu : filteredJilu) {
                    String status = jilu.getJinjiZhuangtai();
                    if ("待审核".equals(status)) {
                        pendingCount++;
                    } else if ("已通过".equals(status)) {
                        approvedCount++;
                    } else if ("已驳回".equals(status)) {
                        rejectedCount++;
                    }
                }
            }
            log.info("待审核：{}，已通过：{}，已驳回：{}", pendingCount, approvedCount, rejectedCount);
            
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalGuanxi", totalGuanxi);
            stats.put("activeGuanxi", activeGuanxi);
            stats.put("totalJilu", totalJilu);
            stats.put("pendingCount", pendingCount);
            stats.put("approvedCount", approvedCount);
            stats.put("rejectedCount", rejectedCount);
            stats.put("totalCount", totalJilu); // 添加总记录数字段
            
            return R.ok().put("data", stats);
        } catch (Exception e) {
            log.error("获取晋级统计异常", e);
            return R.error("获取统计信息失败");
        }
    }
}
