package com.controller;

import com.service.CacheMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存监控控制器 - 产品化版本
 * 
 * 【功能说明】
 * 提供缓存系统的监控和管理接口，用于：
 * 1. 查看缓存统计信息（命中率、操作次数等）
 * 2. 检查 Redis 连接状态
 * 3. 获取 Redis 服务器信息
 * 4. 重置统计数据
 * 
 * 【访问方式】
 * GET  /api/cache/stats      - 获取缓存统计
 * GET  /api/cache/health      - 健康检查
 * GET  /api/cache/info        - Redis 服务器信息
 * POST /api/cache/reset       - 重置统计数据
 * 
 * @author 产品化优化版本
 * @date 2026-04-06
 */
@RestController
@RequestMapping("/api/cache")
public class CacheMonitorController {

    @Autowired(required = false)
    private CacheMonitorService cacheMonitorService;

    /**
     * 获取缓存统计信息
     * 
     * @return 包含命中率、操作次数等指标的 JSON
     */
    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> result = new HashMap<>();
        
        if (cacheMonitorService == null) {
            result.put("success", false);
            result.put("message", "缓存监控服务未启用");
            return result;
        }

        try {
            Map<String, Object> stats = cacheMonitorService.getCacheStats();
            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 缓存健康检查
     * 
     * @return 健康状态信息
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        
        if (cacheMonitorService == null) {
            result.put("status", "unavailable");
            result.put("message", "缓存监控服务未启用");
            return result;
        }

        boolean connected = cacheMonitorService.checkRedisConnection();
        Map<String, Object> stats = cacheMonitorService.getCacheStats();

        result.put("status", connected ? "healthy" : "unhealthy");
        result.put("redisConnected", connected);
        result.put("cacheStats", stats);

        return result;
    }

    /**
     * 获取 Redis 服务器详细信息
     * 
     * @return Redis 服务器信息
     */
    @GetMapping("/info")
    public Map<String, Object> getRedisInfo() {
        Map<String, Object> result = new HashMap<>();
        
        if (cacheMonitorService == null) {
            result.put("success", false);
            result.put("message", "缓存监控服务未启用");
            return result;
        }

        try {
            Map<String, String> info = cacheMonitorService.getRedisInfo();
            result.put("success", true);
            result.put("data", info);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取 Redis 信息失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 重置缓存统计数据
     * 
     * @return 操作结果
     */
    @PostMapping("/reset")
    public Map<String, Object> resetStats() {
        Map<String, Object> result = new HashMap<>();
        
        if (cacheMonitorService == null) {
            result.put("success", false);
            result.put("message", "缓存监控服务未启用");
            return result;
        }

        try {
            cacheMonitorService.resetStats();
            result.put("success", true);
            result.put("message", "统计数据已重置");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重置失败: " + e.getMessage());
        }

        return result;
    }
}
