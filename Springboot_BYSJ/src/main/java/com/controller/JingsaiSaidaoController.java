package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiSaidaoEntity;
import com.entity.JingsaibaomingEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiSaidaoService;
import com.service.JingsaibaomingService;
import com.service.JingsaixinxiService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/jingsai/saidao")
public class JingsaiSaidaoController {
    @Autowired private JingsaiSaidaoService saidaoService;
    @Autowired private JingsaixinxiService jingsaixinxiService;
    @Autowired private JingsaibaomingService jingsaibaomingService;

    @OperationLog("保存竞赛赛道")
    @PostMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaiSaidaoEntity saidao) {
        try {
            if (!StringUtils.hasText(saidao.getSaidaoMingcheng())) {
                log.warn("保存赛道失败：赛道名称为空");
                return R.error("赛道名称不能为空");
            }
            
            // 联动：确保赛道中的竞赛名称与竞赛信息一致
            if (saidao.getJingsaiId() != null && !StringUtils.hasText(saidao.getJingsaimingcheng())) {
                JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(saidao.getJingsaiId());
                if (jingsai != null) {
                    saidao.setJingsaimingcheng(jingsai.getJingsaimingcheng());
                    log.info("自动填充赛道竞赛名称：{}", jingsai.getJingsaimingcheng());
                }
            }
            
            // 联动：如果竞赛的“是否有赛道”为空或为“否”，自动更新为“是"
            if (saidao.getJingsaiId() != null) {
                JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(saidao.getJingsaiId());
                if (jingsai != null && !"是".equals(jingsai.getShifouYouSaidao())) {
                    jingsai.setShifouYouSaidao("是");
                    jingsaixinxiService.updateById(jingsai);
                    log.info("联动更新竞赛的“是否有赛道”标志为“是”，竞赛ID: {}", saidao.getJingsaiId());
                }
            }
            
            saidao.setId(IdWorker.getId());
            saidaoService.insert(saidao);
            log.info("保存赛道成功，ID: {}, 名称: {}", saidao.getId(), saidao.getSaidaoMingcheng());
            return R.ok("添加成功，竞赛的赛道标志已自动更新");
        } catch (Exception e) {
            log.error("保存赛道异常", e);
            return R.error("添加失败，请重试");
        }
    }

    @OperationLog("更新竞赛赛道")
    @PostMapping("/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaiSaidaoEntity saidao) {
        try {
            if (saidao.getId() == null || saidao.getId() <= 0) {
                log.warn("更新赛道失败：ID非法");
                return R.error("赛道ID非法");
            }
            
            // 查询原赛道信息（用于对比变更）
            JingsaiSaidaoEntity existingSaidao = saidaoService.selectById(saidao.getId());
            if (existingSaidao == null) {
                return R.error("赛道不存在");
            }
            
            // 联动1：确保赛道中的竞赛名称与竞赛信息一致
            if (saidao.getJingsaiId() != null) {
                JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(saidao.getJingsaiId());
                if (jingsai != null) {
                    saidao.setJingsaimingcheng(jingsai.getJingsaimingcheng());
                    log.debug("联动：同步赛道竞赛名称为 {}", jingsai.getJingsaimingcheng());
                }
            }
            
            // 联动2：参赛类型变更时，同步更新报名记录的参赛类型
            String newCansaiLeixing = saidao.getCansaiLeixing();
            String oldCansaiLeixing = existingSaidao.getCansaiLeixing();
            StringBuilder msg = new StringBuilder("更新成功");
            
            if (newCansaiLeixing != null && !newCansaiLeixing.equals(oldCansaiLeixing)) {
                try {
                    EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                    baomingEw.eq("saidao_id", saidao.getId());
                    List<JingsaibaomingEntity> baomingList = jingsaibaomingService.selectList(baomingEw);
                    if (baomingList != null && !baomingList.isEmpty()) {
                        for (JingsaibaomingEntity baoming : baomingList) {
                            baoming.setCansaileixing(newCansaiLeixing);
                            jingsaibaomingService.updateById(baoming);
                        }
                        msg.append("，报名记录参赛类型已同步更新(" + baomingList.size() + "条)");
                        log.info("联动：赛道参赛类型变更，同步更新报名记录，赛道ID: {}", saidao.getId());
                    }
                } catch (Exception e) {
                    log.error("联动更新报名记录参赛类型失败", e);
                    msg.append("（报名记录参赛类型同步失败）");
                }
            }
            
            // 联动3：赛道状态从“是”变为“否”时，提示教师注意
            String newIsActive = saidao.getIsActive();
            String oldIsActive = existingSaidao.getIsActive();
            if ("否".equals(newIsActive) && "是".equals(oldIsActive)) {
                msg.append("，注意：赛道已停用，相关报名记录可能需要调整");
                log.info("联动：赛道 {} 从活跃变为停用", saidao.getSaidaoMingcheng());
            }
            
            saidaoService.updateById(saidao);
            log.info("更新赛道成功，ID: {}", saidao.getId());
            return R.ok(msg.toString());
        } catch (Exception e) {
            log.error("更新赛道异常", e);
            return R.error("更新失败，请重试");
        }
    }

    @OperationLog("删除竞赛赛道")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        try {
            if (ids == null || ids.length == 0) {
                log.warn("删除赛道失败：ID数组为空");
                return R.error("请选择要删除的赛道");
            }
            
            StringBuilder msg = new StringBuilder("删除成功");
            
            // 联动：删除赛道前，清理报名记录中的saidaoId引用
            for (Long id : ids) {
                try {
                    JingsaiSaidaoEntity saidao = saidaoService.selectById(id);
                    if (saidao == null) continue;
                    
                    // 清理报名记录中的saidaoId引用
                    EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                    baomingEw.eq("saidao_id", id);
                    List<JingsaibaomingEntity> baomingList = jingsaibaomingService.selectList(baomingEw);
                    if (baomingList != null && !baomingList.isEmpty()) {
                        for (JingsaibaomingEntity baoming : baomingList) {
                            baoming.setSaidaoId(null);
                            jingsaibaomingService.updateById(baoming);
                        }
                        msg.append("，清理报名记录赛道引用(" + baomingList.size() + "条)");
                        log.info("联动：删除赛道时清理报名记录saidaoId，赛道ID: {}，清理{}条", id, baomingList.size());
                    }
                    
                    // 联动：检查该竞赛是否还有其他赛道，如果没有则更新竞赛的“是否有赛道”标志
                    if (saidao.getJingsaiId() != null) {
                        EntityWrapper<JingsaiSaidaoEntity> remainingEw = new EntityWrapper<>();
                        remainingEw.eq("jingsai_id", saidao.getJingsaiId());
                        remainingEw.notIn("id", Arrays.asList(ids));
                        int remainingCount = saidaoService.selectCount(remainingEw);
                        if (remainingCount <= 0) {
                            JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(saidao.getJingsaiId());
                            if (jingsai != null && "是".equals(jingsai.getShifouYouSaidao())) {
                                jingsai.setShifouYouSaidao("否");
                                jingsaixinxiService.updateById(jingsai);
                                msg.append("，竞赛的赛道标志已自动更新为“否”");
                                log.info("联动：竞赛无剩余赛道，更新标志为“否”，竞赛ID: {}", saidao.getJingsaiId());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("联动处理赛道删除失败，赛道ID: {}", id, e);
                    msg.append("（联动处理失败）");
                }
            }
            
            saidaoService.deleteBatchIds(java.util.Arrays.asList(ids));
            log.info("删除赛道成功，IDs: {}", (Object) ids);
            return R.ok(msg.toString());
        } catch (Exception e) {
            log.error("删除赛道异常", e);
            return R.error("删除失败，请重试");
        }
    }

    /**
     * 查询赛道列表
     * 教师只能查看自己组织的竞赛的赛道
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("查询赛道列表 - 角色: {}", tableName);
        
        // 教师过滤：只查看自己组织的竞赛的赛道
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询自己组织的竞赛的赛道", gonghao);
            
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(Collectors.toList());
                
                // 使用 EntityWrapper IN 查询替代全表扫描+内存过滤
                EntityWrapper<JingsaiSaidaoEntity> ew = new EntityWrapper<>();
                ew.in("jingsai_id", myJingsaiIds);
                List<JingsaiSaidaoEntity> filteredSaidao = saidaoService.selectList(ew);
                
                log.info("教师 {} 查询到 {} 个竞赛的 {} 个赛道", gonghao, myJingsaiIds.size(), filteredSaidao.size());
                
                int total = filteredSaidao.size();
                int pageSize = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
                int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
                int startIndex = (pageNum - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, total);
                
                List<JingsaiSaidaoEntity> pageList = filteredSaidao.subList(
                    Math.max(0, startIndex), 
                    Math.max(0, endIndex)
                );
                
                PageUtils page = new PageUtils(pageList, total, pageSize, pageNum);
                return R.ok().put("page", page);
            } else {
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                return R.ok().put("page", new PageUtils(new ArrayList<>(), 0, 10, 1));
            }
        }
        
        // 管理员或学生：查询所有
        PageUtils page = saidaoService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 分页查询赛道
     * 教师只能查看自己组织的竞赛的赛道
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("分页查询赛道 - 角色: {}", tableName);
        
        // 教师过滤：只查看自己组织的竞赛的赛道
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 分页查询自己组织的竞赛的赛道", gonghao);
            
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(Collectors.toList());
                
                // 使用 EntityWrapper IN 查询替代全表扫描+内存过滤
                EntityWrapper<JingsaiSaidaoEntity> ew = new EntityWrapper<>();
                ew.in("jingsai_id", myJingsaiIds);
                List<JingsaiSaidaoEntity> filteredSaidao = saidaoService.selectList(ew);
                
                log.info("教师 {} 分页查询到 {} 个竞赛的 {} 个赛道", gonghao, myJingsaiIds.size(), filteredSaidao.size());
                
                int total = filteredSaidao.size();
                int pageSize = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
                int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
                int startIndex = (pageNum - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, total);
                
                List<JingsaiSaidaoEntity> pageList = filteredSaidao.subList(
                    Math.max(0, startIndex), 
                    Math.max(0, endIndex)
                );
                
                PageUtils page = new PageUtils(pageList, total, pageSize, pageNum);
                return R.ok().put("page", page);
            } else {
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                return R.ok().put("page", new PageUtils(new ArrayList<>(), 0, 10, 1));
            }
        }
        
        // 管理员或学生：查询所有
        PageUtils page = saidaoService.queryPage(params);
        return R.ok().put("page", page);
    }

    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().put("jingsaisaidao", saidaoService.selectById(id));
    }

    @IgnoreAuth
    @GetMapping("/list/{jingsaiId}")
    public R listByJingsai(@PathVariable Long jingsaiId, @RequestParam Map<String, Object> params) {
        EntityWrapper<JingsaiSaidaoEntity> ew = new EntityWrapper<>();
        ew.eq("jingsai_id", jingsaiId);
        ew.eq("is_active", "是");
        ew.orderBy("sort_order", true);
        PageUtils page = saidaoService.queryPage(params);
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 获取统计数据
     * 功能：统计赛道总数、活跃赛道数、关联竞赛数
     * 教师只能查看自己创建的竞赛的赛道统计
     * 
     * @param request HTTP请求（获取用户角色信息）
     * @return R 统一返回结果，包含统计信息
     */
    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        try {
            log.info("========== 赛道统计数据查询开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.info("当前用户角色tableName：{}", tableName);
            
            Map<String, Object> stats = new java.util.HashMap<>();
            
            // 构建基础查询条件
            List<Long> myJingsaiIds = null;
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    myJingsaiIds = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getId)
                            .collect(Collectors.toList());
                } else {
                    // 教师无竞赛，返回空统计
                    stats.put("totalSaidao", 0);
                    stats.put("activeSaidao", 0);
                    stats.put("jingsaiCount", 0);
                    return R.ok().put("data", stats);
                }
            }
            
            // 统计赛道总数
            EntityWrapper<JingsaiSaidaoEntity> baseEw = new EntityWrapper<>();
            if (myJingsaiIds != null) baseEw.in("jingsai_id", myJingsaiIds);
            int totalCount = saidaoService.selectCount(baseEw);
            log.info("赛道总数：{}", totalCount);
            stats.put("totalSaidao", totalCount);
            
            // 统计活跃赛道数
            EntityWrapper<JingsaiSaidaoEntity> activeEw = new EntityWrapper<>();
            if (myJingsaiIds != null) activeEw.in("jingsai_id", myJingsaiIds);
            activeEw.eq("is_active", "是");
            int activeCount = saidaoService.selectCount(activeEw);
            log.info("活跃赛道数：{}", activeCount);
            stats.put("activeSaidao", activeCount);
            
            // 统计关联竞赛数 - 需要查询实际数据做distinct
            EntityWrapper<JingsaiSaidaoEntity> jingsaiCountEw = new EntityWrapper<>();
            if (myJingsaiIds != null) jingsaiCountEw.in("jingsai_id", myJingsaiIds);
            jingsaiCountEw.groupBy("jingsai_id");
            // 使用selectCount配合groupBy在MyBatis-Plus 2.x中不直接支持distinct count
            // 改为selectList只查jingsai_id字段，减少数据量
            jingsaiCountEw.setSqlSelect("jingsai_id");
            List<JingsaiSaidaoEntity> distinctList = saidaoService.selectList(jingsaiCountEw);
            long jingsaiCount = distinctList.stream()
                .map(JingsaiSaidaoEntity::getJingsaiId)
                .filter(id -> id != null)
                .distinct()
                .count();
            stats.put("jingsaiCount", jingsaiCount);
            
            log.info("赛道统计数据查询成功");
            return R.ok().put("data", stats);
            
        } catch (Exception e) {
            log.error("赛道统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }
}
