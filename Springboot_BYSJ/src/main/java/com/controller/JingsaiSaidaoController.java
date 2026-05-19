package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiSaidaoEntity;
import com.service.JingsaiSaidaoService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/jingsai/saidao")
public class JingsaiSaidaoController {
    @Autowired private JingsaiSaidaoService saidaoService;

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

    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = saidaoService.queryPage(params);
        return R.ok().put("page", page);
    }

    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
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
     * 
     * @return R 统一返回结果，包含统计信息
     */
    @RequestMapping("/statistics")
    public R statistics() {
        try {
            log.info("========== 赛道统计数据查询开始 ==========");
            Map<String, Object> stats = new java.util.HashMap<>();
            
            // 使用更可靠的查询方式：先查询所有，再过滤
            List<JingsaiSaidaoEntity> allSaidao = saidaoService.selectList(null);
            
            // 1. 统计赛道总数
            int totalCount = allSaidao != null ? allSaidao.size() : 0;
            log.info("赛道总数：{}", totalCount);
            stats.put("totalSaidao", totalCount);
            
            // 2. 统计活跃赛道数
            int activeCount = 0;
            if (allSaidao != null) {
                for (JingsaiSaidaoEntity saidao : allSaidao) {
                    if ("是".equals(saidao.getIsActive())) {
                        activeCount++;
                    }
                }
            }
            log.info("活跃赛道数：{}", activeCount);
            stats.put("activeSaidao", activeCount);
            
            // 3. 统计关联竞赛数（去重）
            long jingsaiCount = allSaidao.stream()
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
