package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiJibieBanbenEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiJibieBanbenService;
import com.service.JingsaixinxiService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    /**
     * 分页查询级别版本
     * 教师只能查看自己创建的竞赛的级别版本
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("查询级别版本列表 - 角色: {}", tableName);
        
        // 先查询所有数据
        List<JingsaiJibieBanbenEntity> allRecords = jibieBanbenService.selectList(null);
        
        // 教师过滤：只查看自己创建的竞赛的级别版本
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询级别版本", gonghao);
            
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                List<Long> jingsaiIds = new ArrayList<>();
                for (JingsaixinxiEntity jingsai : myJingsaiList) {
                    jingsaiIds.add(jingsai.getId());
                }
                
                // 内存过滤：只保留教师创建的竞赛的级别版本
                List<JingsaiJibieBanbenEntity> filteredRecords = new ArrayList<>();
                for (JingsaiJibieBanbenEntity record : allRecords) {
                    if (jingsaiIds.contains(record.getJingsaiId())) {
                        filteredRecords.add(record);
                    }
                }
                
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的级别版本，竞赛IDs: {}", gonghao, jingsaiIds.size(), jingsaiIds);
                
                // 构建分页结果
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
        
        // 先查询所有数据
        List<JingsaiJibieBanbenEntity> allRecords = jibieBanbenService.selectList(null);
        
        // 教师过滤：只查看自己创建的竞赛的级别版本
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询级别版本", gonghao);
            
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                List<Long> jingsaiIds = new ArrayList<>();
                for (JingsaixinxiEntity jingsai : myJingsaiList) {
                    jingsaiIds.add(jingsai.getId());
                }
                
                // 内存过滤：只保留教师创建的竞赛的级别版本
                List<JingsaiJibieBanbenEntity> filteredRecords = new ArrayList<>();
                for (JingsaiJibieBanbenEntity record : allRecords) {
                    if (jingsaiIds.contains(record.getJingsaiId())) {
                        filteredRecords.add(record);
                    }
                }
                
                log.info("教师 {} 只能查看自己创建的 {} 个竞赛的级别版本，竞赛IDs: {}", gonghao, jingsaiIds.size(), jingsaiIds);
                
                // 构建分页结果
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
     */
    @OperationLog("保存竞赛级别版本")
    @PostMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaiJibieBanbenEntity banben, HttpServletRequest request) {
        String caozuoRen = (String) request.getSession().getAttribute("username");
        return jibieBanbenService.saveBanben(banben, caozuoRen);
    }

    /**
     * 更新级别版本
     */
    @OperationLog("更新竞赛级别版本")
    @PostMapping("/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaiJibieBanbenEntity banben) {
        jibieBanbenService.updateById(banben);
        return R.ok("更新成功");
    }

    /**
     * 删除级别版本
     */
    @OperationLog("删除竞赛级别版本")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        jibieBanbenService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok("删除成功");
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
            
            // 使用更可靠的查询方式：先查询所有，再过滤
            List<JingsaiJibieBanbenEntity> allRecords = jibieBanbenService.selectList(null);
            
            // 根据角色过滤数据
            List<JingsaiJibieBanbenEntity> filteredRecords = allRecords;
            
            if ("jiaoshi".equals(tableName)) {
                // 教师：只查看自己竞赛的级别版本
                if (!myJingsaiIds.isEmpty()) {
                    filteredRecords = new ArrayList<>();
                    if (allRecords != null) {
                        for (JingsaiJibieBanbenEntity record : allRecords) {
                            if (myJingsaiIds.contains(record.getJingsaiId())) {
                                filteredRecords.add(record);
                            }
                        }
                    }
                } else {
                    filteredRecords = new ArrayList<>();
                }
            }
            
            // 总版本数
            int totalBanben = filteredRecords != null ? filteredRecords.size() : 0;
            log.info("级别版本总数：{}", totalBanben);
            
            // 国家级数量
            int guojiajiCount = 0;
            int currentBanben = 0;
            if (filteredRecords != null) {
                for (JingsaiJibieBanbenEntity record : filteredRecords) {
                    if ("国家级".equals(record.getJibie())) {
                        guojiajiCount++;
                    }
                    if ("是".equals(record.getIsCurrent())) {
                        currentBanben++;
                    }
                }
            }
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
