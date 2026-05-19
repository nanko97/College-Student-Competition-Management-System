package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiJibieBanbenEntity;
import com.service.JingsaiJibieBanbenService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * 分页查询级别版本
     */
    @IgnoreAuth
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询级别版本列表
     */
    @IgnoreAuth
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
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
     */
    @GetMapping("/statistics")
    public R getStatistics() {
        try {
            log.info("========== 级别版本统计数据查询开始 ==========");
            // 使用更可靠的查询方式：先查询所有，再过滤
            List<JingsaiJibieBanbenEntity> allRecords = jibieBanbenService.selectList(null);
            
            // 总版本数
            int totalBanben = allRecords != null ? allRecords.size() : 0;
            log.info("级别版本总数：{}", totalBanben);
            
            // 国家级数量
            int guojiajiCount = 0;
            int currentBanben = 0;
            if (allRecords != null) {
                for (JingsaiJibieBanbenEntity record : allRecords) {
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
