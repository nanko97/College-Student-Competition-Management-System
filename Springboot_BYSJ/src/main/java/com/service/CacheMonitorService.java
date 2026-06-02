package com.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存监控服务
 */
@Service
@Slf4j
public class CacheMonitorService {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存统计数据
    private final AtomicLong hitCount = new AtomicLong(0);      // 命中次数
    private final AtomicLong missCount = new AtomicLong(0);     // 未命中次数
    private final AtomicLong setCount = new AtomicLong(0);      // 写入次数
    private final AtomicLong deleteCount = new AtomicLong(0);   // 删除次数
    private final AtomicLong errorCount = new AtomicLong(0);    // 错误次数

    /**
     * 记录缓存命中
     */
    public void recordHit() {
        hitCount.incrementAndGet();
    }

    /**
     * 记录缓存未命中
     */
    public void recordMiss() {
        missCount.incrementAndGet();
    }

    /**
     * 记录缓存写入
     */
    public void recordSet() {
        setCount.incrementAndGet();
    }

    /**
     * 记录缓存删除
     */
    public void recordDelete() {
        deleteCount.incrementAndGet();
    }

    /**
     * 记录错误
     */
    public void recordError() {
        errorCount.incrementAndGet();
    }

    /**
     * 获取缓存统计信息
     * 
     * @return 包含所有统计指标的 Map
     */
    public Map<String, Object> getCacheStats() {
        long hits = hitCount.get();
        long misses = missCount.get();
        long total = hits + misses;
        
        double hitRate = total > 0 ? (double) hits / total * 100 : 0;
        double missRate = total > 0 ? (double) misses / total * 100 : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("hitCount", hits);
        stats.put("missCount", misses);
        stats.put("setCount", setCount.get());
        stats.put("deleteCount", deleteCount.get());
        stats.put("errorCount", errorCount.get());
        stats.put("totalRequests", total);
        stats.put("hitRate", String.format("%.2f%%", hitRate));
        stats.put("missRate", String.format("%.2f%%", missRate));
        
        // 健康状态评估
        String healthStatus = evaluateHealth(hitRate, errorCount.get());
        stats.put("healthStatus", healthStatus);

        return stats;
    }

    /**
     * 检查 Redis 连接状态
     * 
     * @return true-连接正常，false-连接异常
     */
    public boolean checkRedisConnection() {
        if (redisTemplate == null) {
            log.warn("RedisTemplate 未初始化");
            return false;
        }
        
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            log.error("Redis 连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取 Redis 服务器信息
     * 
     * @return Redis 服务器基本信息
     */
    public Map<String, String> getRedisInfo() {
        Map<String, String> info = new HashMap<>();
        
        if (!checkRedisConnection()) {
            info.put("status", "disconnected");
            info.put("message", "无法连接到 Redis 服务器");
            return info;
        }

        try {
            Properties properties = redisTemplate.getConnectionFactory()
                    .getConnection().info();
            
            info.put("status", "connected");
            info.put("version", properties.getProperty("redis_version", "unknown"));
            info.put("usedMemory", formatMemory(properties.getProperty("used_memory_human", "0B")));
            info.put("connectedClients", properties.getProperty("connected_clients", "0"));
            info.put("uptime", properties.getProperty("uptime_in_days", "0") + " days");
            
        } catch (Exception e) {
            log.error("获取 Redis 信息失败", e);
            info.put("status", "error");
            info.put("message", e.getMessage());
        }

        return info;
    }

    /**
     * 重置所有统计数据
     */
    public void resetStats() {
        hitCount.set(0);
        missCount.set(0);
        setCount.set(0);
        deleteCount.set(0);
        errorCount.set(0);
        log.info("缓存统计数据已重置");
    }

    /**
     * 评估缓存健康状态
     */
    private String evaluateHealth(double hitRate, long errors) {
        if (errors > 100) {
            return "CRITICAL";
        } else if (errors > 10) {
            return "WARNING";
        } else if (hitRate < 50) {
            return "POOR";
        } else if (hitRate < 80) {
            return "FAIR";
        } else {
            return "GOOD";
        }
    }

    /**
     * 格式化内存大小显示
     */
    private String formatMemory(String memoryStr) {
        return memoryStr; // 已经是人类可读格式
    }

    /**
     * 缓存统计 DTO
     */
    @Data
    public static class CacheStats {
        private Long hitCount;
        private Long missCount;
        private Long setCount;
        private Long deleteCount;
        private Long errorCount;
        private Long totalRequests;
        private String hitRate;
        private String missRate;
        private String healthStatus;
    }
}
