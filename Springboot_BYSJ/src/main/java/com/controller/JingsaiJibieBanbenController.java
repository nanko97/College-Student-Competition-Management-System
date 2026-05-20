package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiJibieBanbenEntity;
import com.entity.JingsaibaomingEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiJibieBanbenService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 竞赛级别版本管理控制器（独立路径）
 * 提供 /jingsai/jibiebanben/* 路径的API接口
 */
@RestController
@RequestMapping("/jingsai/jibiebanben")
@Slf4j
public class JingsaiJibieBanbenController {

    @Autowired
    private JingsaiJibieBanbenService jibieBanbenService;

    @Autowired
    private JingsaixinxiService jingsaixinxiService;

    @Autowired
    private JingsaibaomingService jingsaibaomingService;

    /**
     * 分页查询级别版本
     * 教师只能查看自己创建的竞赛的级别版本
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("查询级别版本列表 - 角色: {}", tableName);
        
        // 教师过滤：只查看自己创建的竞赛的级别版本
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询级别版本", gonghao);
            
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                List<Long> jingsaiIds = new ArrayList<>();
                for (JingsaixinxiEntity jingsai : myJingsaiList) {
                    jingsaiIds.add(jingsai.getId());
                }
                
                // 使用 EntityWrapper IN 查询替代全表扫描+内存过滤
                EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
                ew.in("jingsai_id", jingsaiIds);
                List<JingsaiJibieBanbenEntity> filteredRecords = jibieBanbenService.selectList(ew);
                
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的级别版本", gonghao, jingsaiIds.size());
                
                int total = filteredRecords.size();
                int pageSize = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
                int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
                int startIndex = (pageNum - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, total);
                
                List<JingsaiJibieBanbenEntity> pageList = filteredRecords.subList(
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
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询级别版本列表
     * 教师只能查看自己创建的竞赛的级别版本
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("查询级别版本列表 - 角色: {}", tableName);
        
        // 教师过滤：只查看自己创建的竞赛的级别版本
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询级别版本", gonghao);
            
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                List<Long> jingsaiIds = new ArrayList<>();
                for (JingsaixinxiEntity jingsai : myJingsaiList) {
                    jingsaiIds.add(jingsai.getId());
                }
                
                // 使用 EntityWrapper IN 查询替代全表扫描+内存过滤
                EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
                ew.in("jingsai_id", jingsaiIds);
                List<JingsaiJibieBanbenEntity> filteredRecords = jibieBanbenService.selectList(ew);
                
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的级别版本", gonghao, jingsaiIds.size());
                
                int total = filteredRecords.size();
                int pageSize = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
                int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
                int startIndex = (pageNum - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, total);
                
                List<JingsaiJibieBanbenEntity> pageList = filteredRecords.subList(
                    Math.max(0, startIndex), 
                    Math.max(0, endIndex)
                );
                
                PageUtils page = new PageUtils(pageList, total, pageSize, pageNum);
                return R.ok().put("page", page);
            } else {
                log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                return R.ok().put("data", new ArrayList<>());
            }
        }
        
        // 管理员或学生：查询所有
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询级别版本详情
     */
    @IgnoreAuth
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().put("jingsaijibiebanben", jibieBanbenService.selectById(id));
    }

    /**
     * 保存级别版本
     * 联动：saveBanben已处理版本切换，此处仅额外处理竞赛级别同步
     */
    @OperationLog("保存竞赛级别版本")
    @PostMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaiJibieBanbenEntity banben, HttpServletRequest request) {
        String caozuoRen = (String) request.getSession().getAttribute("username");
        
        // 联动1：确保级别版本中的竞赛名称与竞赛信息一致
        if (banben.getJingsaiId() != null) {
            JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
            if (jingsai != null) {
                banben.setJingsaimingcheng(jingsai.getJingsaimingcheng());
                log.info("联动：自动填充级别版本竞赛名称：{}", jingsai.getJingsaimingcheng());
            }
        }
        
        // 联动2：调用saveBanben（它已处理版本切换和插入逻辑）
        R result = jibieBanbenService.saveBanben(banben, caozuoRen);
        
        // 联动3：saveBanben成功后，更新竞赛的竞赛级别字段
        // saveBanben会将新版本设为is_current=是，所以竞赛级别应该同步
        StringBuilder extraMsg = new StringBuilder();
        if (result.get("code") != null && Integer.parseInt(result.get("code").toString()) == 0) {
            if (banben.getJingsaiId() != null && StringUtils.hasText(banben.getJibie())) {
                try {
                    JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
                    if (jingsai != null) {
                        String oldJibie = jingsai.getJingsaiJibie();
                        String newJibie = banben.getJibie();
                        // 只有级别实际变更时才更新
                        if (!newJibie.equals(oldJibie)) {
                            jingsai.setJingsaiJibie(newJibie);
                            if (banben.getJingsaiNianfen() != null) {
                                jingsai.setNianfen(banben.getJingsaiNianfen());
                            }
                            jingsaixinxiService.updateById(jingsai);
                            extraMsg.append("，竞赛级别已同步更新为" + newJibie);
                            log.info("联动：保存级别版本后更新竞赛级别，竞赛ID: {}，旧级别: {} -> 新级别: {}", banben.getJingsaiId(), oldJibie, newJibie);
                        }
                    }
                } catch (Exception e) {
                    log.error("联动更新竞赛级别失败", e);
                    extraMsg.append("（竞赛级别联动失败）");
                }
            }
            
            String originalMsg = result.get("msg") != null ? result.get("msg").toString() : "级别版本创建成功";
            if (extraMsg.length() > 0) {
                return R.ok(originalMsg + extraMsg.toString());
            }
            return result;
        }
        return result;
    }

    /**
     * 更新级别版本
     * 联动：如果更新了级别或当前版本状态，同步更新竞赛的竞赛级别
     */
    @OperationLog("更新竞赛级别版本")
    @PostMapping("/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaiJibieBanbenEntity banben) {
        if (banben.getId() == null || banben.getId() <= 0) {
            return R.error("级别版本ID非法");
        }
        
        // 查询原级别版本信息（用于对比变更）
        JingsaiJibieBanbenEntity existingBanben = jibieBanbenService.selectById(banben.getId());
        if (existingBanben == null) {
            return R.error("级别版本不存在");
        }
        
        // 联动1：确保级别版本中的竞赛名称与竞赛信息一致
        if (banben.getJingsaiId() != null) {
            JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
            if (jingsai != null) {
                banben.setJingsaimingcheng(jingsai.getJingsaimingcheng());
                log.debug("联动：同步级别版本竞赛名称为 {}", jingsai.getJingsaimingcheng());
            }
        }
        
        StringBuilder msg = new StringBuilder("更新成功");
        
        // 联动2：当前版本标志变更
        String newIsCurrent = banben.getIsCurrent();
        String oldIsCurrent = existingBanben.getIsCurrent();
        
        // 如果设为当前版本，将其他版本设为非当前
        if ("是".equals(newIsCurrent) && ("否".equals(oldIsCurrent) || oldIsCurrent == null) && banben.getJingsaiId() != null) {
            try {
                EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
                ew.eq("jingsai_id", banben.getJingsaiId());
                ew.eq("is_current", "是");
                ew.notIn("id", Arrays.asList(banben.getId()));
                List<JingsaiJibieBanbenEntity> currentList = jibieBanbenService.selectList(ew);
                if (currentList != null && !currentList.isEmpty()) {
                    for (JingsaiJibieBanbenEntity oldCurrent : currentList) {
                        oldCurrent.setIsCurrent("否");
                        jibieBanbenService.updateById(oldCurrent);
                    }
                    log.info("联动：将其他{}个版本设为非当前，竞赛ID: {}", currentList.size(), banben.getJingsaiId());
                    msg.append("，已将其他" + currentList.size() + "个版本设为非当前版本");
                }
                
                // 更新竞赛级别
                if (StringUtils.hasText(banben.getJibie())) {
                    JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
                    if (jingsai != null) {
                        jingsai.setJingsaiJibie(banben.getJibie());
                        if (banben.getJingsaiNianfen() != null) {
                            jingsai.setNianfen(banben.getJingsaiNianfen());
                        }
                        jingsaixinxiService.updateById(jingsai);
                        msg.append("，竞赛级别已同步更新为" + banben.getJibie());
                        log.info("联动：更新竞赛级别为 {}，竞赛ID: {}", banben.getJibie(), banben.getJingsaiId());
                    }
                }
            } catch (Exception e) {
                log.error("联动更新当前版本标志失败", e);
                msg.append("（当前版本联动失败）");
            }
        }
        
        // 如果从当前版本变为非当前版本
        if ("否".equals(newIsCurrent) && "是".equals(oldIsCurrent) && banben.getJingsaiId() != null) {
            try {
                // 查找是否还有其他当前版本
                EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
                ew.eq("jingsai_id", banben.getJingsaiId());
                ew.eq("is_current", "是");
                ew.notIn("id", Arrays.asList(banben.getId()));
                JingsaiJibieBanbenEntity newCurrent = jibieBanbenService.selectOne(ew);
                if (newCurrent != null && StringUtils.hasText(newCurrent.getJibie())) {
                    // 更新竞赛级别为新的当前版本的级别
                    JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
                    if (jingsai != null) {
                        jingsai.setJingsaiJibie(newCurrent.getJibie());
                        jingsaixinxiService.updateById(jingsai);
                        msg.append("，竞赛级别已自动同步为新的当前版本级别(" + newCurrent.getJibie() + ")");
                        log.info("联动：当前版本变更，更新竞赛级别为 {}", newCurrent.getJibie());
                    }
                }
            } catch (Exception e) {
                log.error("联动更新竞赛级别失败", e);
                msg.append("（竞赛级别联动失败）");
            }
        }
        
        // 联动3：当前版本的级别变更时，同步更新竞赛级别
        if ("是".equals(banben.getIsCurrent()) && banben.getJibie() != null && !banben.getJibie().equals(existingBanben.getJibie()) && banben.getJingsaiId() != null) {
            try {
                JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
                if (jingsai != null) {
                    jingsai.setJingsaiJibie(banben.getJibie());
                    if (banben.getJingsaiNianfen() != null) {
                        jingsai.setNianfen(banben.getJingsaiNianfen());
                    }
                    jingsaixinxiService.updateById(jingsai);
                    msg.append("，竞赛级别已同步更新为" + banben.getJibie());
                    log.info("联动：当前版本级别变更，同步更新竞赛级别，竞赛ID: {}", banben.getJingsaiId());
                }
            } catch (Exception e) {
                log.error("联动更新竞赛级别失败", e);
                msg.append("（竞赛级别联动失败）");
            }
        }
        
        jibieBanbenService.updateById(banben);
        return R.ok(msg.toString());
    }

    /**
     * 删除级别版本
     * 联动：如果删除的是当前版本，自动将下一个版本设为当前版本，并更新竞赛级别
     */
    @OperationLog("删除竞赛级别版本")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.error("请选择要删除的级别版本");
        }
        
        StringBuilder msg = new StringBuilder("删除成功");
        
        // 联动：检查删除的版本中是否有当前版本
        for (Long id : ids) {
            try {
                JingsaiJibieBanbenEntity banben = jibieBanbenService.selectById(id);
                if (banben == null) continue;
                
                // 如果删除的是当前版本，需要处理联动
                if ("是".equals(banben.getIsCurrent()) && banben.getJingsaiId() != null) {
                    // 将同一竞赛中最新的非当前版本设为当前版本
                    EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
                    ew.eq("jingsai_id", banben.getJingsaiId());
                    ew.eq("is_current", "否");
                    ew.notIn("id", Arrays.asList(ids));
                    ew.orderBy("addtime", false); // 按创建时间倒序，取最新的
                    List<JingsaiJibieBanbenEntity> remainingList = jibieBanbenService.selectList(ew);
                    
                    if (remainingList != null && !remainingList.isEmpty()) {
                        // 将最新的非当前版本设为当前版本
                        JingsaiJibieBanbenEntity newestBanben = remainingList.get(0);
                        newestBanben.setIsCurrent("是");
                        jibieBanbenService.updateById(newestBanben);
                        msg.append("，已自动将版本" + newestBanben.getJibie() + "设为当前版本");
                        log.info("联动：删除当前版本后，自动将版本 {} 设为当前版本", newestBanben.getJibie());
                        
                        // 更新竞赛级别
                        if (StringUtils.hasText(newestBanben.getJibie())) {
                            JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
                            if (jingsai != null) {
                                jingsai.setJingsaiJibie(newestBanben.getJibie());
                                jingsaixinxiService.updateById(jingsai);
                                msg.append("，竞赛级别已同步更新为" + newestBanben.getJibie());
                                log.info("联动：更新竞赛级别为 {}", newestBanben.getJibie());
                            }
                        }
                    } else {
                        // 没有其他版本了，竞赛级别清空
                        JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(banben.getJingsaiId());
                        if (jingsai != null) {
                            jingsai.setJingsaiJibie(null);
                            jingsaixinxiService.updateById(jingsai);
                            msg.append("，竞赛无剩余版本，竞赛级别已清空");
                            log.info("联动：竞赛无剩余级别版本，级别字段已清空");
                        }
                    }
                }
            } catch (Exception e) {
                log.error("联动处理级别版本删除失败，版本ID: {}", id, e);
                msg.append("（联动处理失败）");
            }
        }
        
        jibieBanbenService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok(msg.toString());
    }

    /**
     * 获取级别版本统计信息
     * 教师只能查看自己创建的竞赛的级别版本统计
     */
    @GetMapping("/statistics")
    public R getStatistics(HttpServletRequest request) {
        try {
            log.info("========== 级别版本统计数据查询开始 ==========");
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.info("当前用户角色tableName：{}", tableName);
            
            // 获取教师相关的竞赛ID列表
            final List<Long> myJingsaiIds = new ArrayList<>();
            
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
            }
            
            // 使用 selectCount 避免全表扫描
            // 教师无竞赛时直接返回空统计
            if ("jiaoshi".equals(tableName) && myJingsaiIds.isEmpty()) {
                Map<String, Object> stats = new java.util.HashMap<>();
                stats.put("totalBanben", 0);
                stats.put("guojiajiCount", 0);
                stats.put("currentBanben", 0);
                return R.ok().put("data", stats);
            }
            
            // 总版本数
            EntityWrapper<JingsaiJibieBanbenEntity> baseEw = new EntityWrapper<>();
            if (!myJingsaiIds.isEmpty()) baseEw.in("jingsai_id", myJingsaiIds);
            int totalBanben = jibieBanbenService.selectCount(baseEw);
            log.info("级别版本总数：{}", totalBanben);
            
            // 国家级数量
            EntityWrapper<JingsaiJibieBanbenEntity> guojiajiEw = new EntityWrapper<>();
            if (!myJingsaiIds.isEmpty()) guojiajiEw.in("jingsai_id", myJingsaiIds);
            guojiajiEw.eq("jibie", "国家级");
            int guojiajiCount = jibieBanbenService.selectCount(guojiajiEw);
            
            // 当前版本数量
            EntityWrapper<JingsaiJibieBanbenEntity> currentEw = new EntityWrapper<>();
            if (!myJingsaiIds.isEmpty()) currentEw.in("jingsai_id", myJingsaiIds);
            currentEw.eq("is_current", "是");
            int currentBanben = jibieBanbenService.selectCount(currentEw);
            log.info("国家级：{}，当前版本：{}", guojiajiCount, currentBanben);
            
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalBanben", totalBanben);
            stats.put("guojiajiCount", guojiajiCount);
            stats.put("currentBanben", currentBanben);
            
            return R.ok().put("data", stats);
        } catch (Exception e) {
            log.error("获取级别版本统计异常", e);
            return R.error("获取统计信息失败");
        }
    }
}
