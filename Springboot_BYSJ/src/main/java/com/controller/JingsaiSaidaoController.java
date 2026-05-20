package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiSaidaoEntity;
import com.entity.JingsaixinxiEntity;
import com.service.JingsaiSaidaoService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/jingsai/saidao")
public class JingsaiSaidaoController {
    @Autowired private JingsaiSaidaoService saidaoService;
    @Autowired private JingsaixinxiService jingsaixinxiService;

    @OperationLog("保存竞赛赛道")
    @PostMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaiSaidaoEntity saidao) {
        try {
            if (!StringUtils.hasText(saidao.getSaidaoMingcheng())) {
                log.warn("保存赛道失败：赛道名称为空");
                return R.error("赛道名称不能为空");
            }
            
            saidao.setId(IdWorker.getId());
            saidaoService.insert(saidao);
            log.info("保存赛道成功，ID: {}, 名称: {}", saidao.getId(), saidao.getSaidaoMingcheng());
            return R.ok("添加成功");
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
            
            saidaoService.updateById(saidao);
            log.info("更新赛道成功，ID: {}", saidao.getId());
            return R.ok("更新成功");
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
            
            saidaoService.deleteBatchIds(java.util.Arrays.asList(ids));
            log.info("删除赛道成功，IDs: {}", (Object) ids);
            return R.ok("删除成功");
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
        // 权限控制：根据用户角色过滤数据
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("查询赛道列表 - 角色: {}", tableName);
        
        // 先查询所有数据
        List<JingsaiSaidaoEntity> allSaidao = saidaoService.selectList(null);
        
        // 教师过滤：只查看自己组织的竞赛的赛道
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 查询自己组织的竞赛的赛道", gonghao);
            
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛 ID 列表
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(Collectors.toList());
                
                // 内存过滤：只保留教师创建的竞赛的赛道
                List<JingsaiSaidaoEntity> filteredSaidao = new ArrayList<>();
                for (JingsaiSaidaoEntity saidao : allSaidao) {
                    if (myJingsaiIds.contains(saidao.getJingsaiId())) {
                        filteredSaidao.add(saidao);
                    }
                }
                
                log.info("教师 {} 查询到 {} 个竞赛的 {} 个赛道", gonghao, myJingsaiIds.size(), filteredSaidao.size());
                
                // 构建分页结果
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
        // 权限控制：根据用户角色过滤数据
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.info("分页查询赛道 - 角色: {}", tableName);
        
        // 先查询所有数据
        List<JingsaiSaidaoEntity> allSaidao = saidaoService.selectList(null);
        
        // 教师过滤：只查看自己组织的竞赛的赛道
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.info("教师 {} 分页查询自己组织的竞赛的赛道", gonghao);
            
            // 查询该教师创建的所有竞赛ID
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                // 提取竞赛 ID 列表
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(Collectors.toList());
                
                // 内存过滤：只保留教师创建的竞赛的赛道
                List<JingsaiSaidaoEntity> filteredSaidao = new ArrayList<>();
                for (JingsaiSaidaoEntity saidao : allSaidao) {
                    if (myJingsaiIds.contains(saidao.getJingsaiId())) {
                        filteredSaidao.add(saidao);
                    }
                }
                
                log.info("教师 {} 分页查询到 {} 个竞赛的 {} 个赛道", gonghao, myJingsaiIds.size(), filteredSaidao.size());
                
                // 构建分页结果
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
            
            // 使用更可靠的查询方式：先查询所有，再过滤
            List<JingsaiSaidaoEntity> allSaidao = saidaoService.selectList(null);
            
            // 教师过滤：只统计自己创建的竞赛的赛道
            List<JingsaiSaidaoEntity> filteredSaidao = allSaidao;
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                log.info("教师角色，工号：{}", gonghao);
                
                // 查询该教师创建的所有竞赛ID
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    List<Long> myJingsaiIds = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getId)
                            .collect(Collectors.toList());
                    
                    // 内存过滤：只保留教师创建的竞赛的赛道
                    filteredSaidao = new ArrayList<>();
                    if (allSaidao != null) {
                        for (JingsaiSaidaoEntity saidao : allSaidao) {
                            if (myJingsaiIds.contains(saidao.getJingsaiId())) {
                                filteredSaidao.add(saidao);
                            }
                        }
                    }
                    log.info("教师 {} 只能查看自己创建的 {} 个竞赛的 {} 个赛道统计", gonghao, myJingsaiIds.size(), filteredSaidao.size());
                } else {
                    filteredSaidao = new ArrayList<>();
                    log.info("教师 {} 没有创建任何竞赛，返回空统计", gonghao);
                }
            }
            
            // 1. 统计赛道总数
            int totalCount = filteredSaidao != null ? filteredSaidao.size() : 0;
            log.info("赛道总数：{}", totalCount);
            stats.put("totalSaidao", totalCount);
            
            // 2. 统计活跃赛道数
            int activeCount = 0;
            if (filteredSaidao != null) {
                for (JingsaiSaidaoEntity saidao : filteredSaidao) {
                    if ("是".equals(saidao.getIsActive())) {
                        activeCount++;
                    }
                }
            }
            log.info("活跃赛道数：{}", activeCount);
            stats.put("activeSaidao", activeCount);
            
            // 3. 统计关联竞赛数（去重）
            long jingsaiCount = filteredSaidao.stream()
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
