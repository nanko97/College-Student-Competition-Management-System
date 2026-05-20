package com.controller;

import com.annotation.OperationLog;
import com.entity.OperationLogEntity;
import com.service.OperationLogService;
import com.utils.PageUtils;
import com.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 操作日志 Controller
 */
@Slf4j
@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/operationlog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @ApiOperation("分页查询操作日志")
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
        PageUtils page = operationLogService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 获取操作日志详情
     */
    @ApiOperation("获取操作日志详情")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        OperationLogEntity logEntity = operationLogService.selectById(id);
        return R.ok().put("data", logEntity);
    }

    /**
     * 删除操作日志（管理员功能）
     */
    @OperationLog(value = "删除操作日志", module = "系统管理", recordParams = false)
    @ApiOperation("删除操作日志")
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id") Long id) {
        operationLogService.deleteById(id);
        return R.ok("删除成功");
    }

    /**
     * 批量删除操作日志
     */
    @OperationLog(value = "批量删除操作日志", module = "系统管理", recordParams = false)
    @ApiOperation("批量删除操作日志")
    @DeleteMapping("/deleteBatch")
    public R deleteBatch(@RequestBody Long[] ids) {
        for (Long id : ids) {
            operationLogService.deleteById(id);
        }
        return R.ok("批量删除成功");
    }

    /**
     * 清理过期日志（保留指定天数）
     */
    @OperationLog(value = "清理过期日志", module = "系统管理")
    @ApiOperation("清理过期日志")
    @PostMapping("/cleanOldLogs")
    public R cleanOldLogs(@RequestBody Map<String, Object> params) {
        int days = params.get("days") != null ? Integer.parseInt(params.get("days").toString()) : 180;
        int deletedCount = operationLogService.deleteOldLogs(days);
        return R.ok("清理完成，共删除" + deletedCount + "条记录");
    }

    /**
     * 获取操作日志统计信息
     */
    @ApiOperation("获取操作日志统计")
    @GetMapping("/statistics")
    public R statistics() {
        // 总日志数
        long totalCount = operationLogService.selectCount(null);
        
        // 今日日志数
        Map<String, Object> todayParams = new java.util.HashMap<>();
        todayParams.put("startTime", java.time.LocalDate.now().atStartOfDay().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        todayParams.put("page", 1);
        todayParams.put("limit", 1);
        PageUtils todayPage = operationLogService.queryPage(todayParams);
        long todayCount = todayPage.getTotal();
        
        // 成功/失败统计
        Map<String, Object> successParams = new java.util.HashMap<>();
        successParams.put("status", "成功");
        successParams.put("page", 1);
        successParams.put("limit", 1);
        PageUtils successPage = operationLogService.queryPage(successParams);
        long successCount = successPage.getTotal();
        
        Map<String, Object> failParams = new java.util.HashMap<>();
        failParams.put("status", "失败");
        failParams.put("page", 1);
        failParams.put("limit", 1);
        PageUtils failPage = operationLogService.queryPage(failParams);
        long failCount = failPage.getTotal();
        
        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("total", totalCount);  // 前端使用 total 字段名
        statistics.put("totalCount", totalCount);
        statistics.put("todayCount", todayCount);
        statistics.put("successCount", successCount);
        statistics.put("failCount", failCount);
        
        return R.ok().put("data", statistics);
    }
}
