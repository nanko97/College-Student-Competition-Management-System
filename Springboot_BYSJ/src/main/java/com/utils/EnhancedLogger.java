package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 增强日志工具类
 */
@Slf4j
public class EnhancedLogger {

    // ==================== 专用 Logger ====================
    
    /** API 访问日志 Logger */
    private static final Logger API_ACCESS_LOGGER = LoggerFactory.getLogger("API_ACCESS");
    
    /** 慢查询日志 Logger */
    private static final Logger SLOW_QUERY_LOGGER = LoggerFactory.getLogger("SLOW_QUERY");
    
    /** 性能监控日志 Logger */
    private static final Logger PERFORMANCE_LOGGER = LoggerFactory.getLogger("PERFORMANCE");
    
    /** 慢查询阈值（毫秒） */
    private static final long SLOW_QUERY_THRESHOLD_MS = 1000;
    
    /** 性能警告阈值（毫秒） */
    private static final long PERFORMANCE_WARNING_MS = 500;

    // ==================== API 访问日志 ====================

    /**
     * 记录 API 访问日志
     * 
     * @param method HTTP 方法 (GET/POST/PUT/DELETE)
     * @param uri 请求 URI
     * @param ip 客户端 IP
     * @param statusCode 响应状态码
     * @param durationMs 请求处理时间（毫秒）
     */
    public static void logApiAccess(String method, String uri, String ip, int statusCode, long durationMs) {
        String slowTag = durationMs > SLOW_QUERY_THRESHOLD_MS ? " [SLOW]" : "";
        API_ACCESS_LOGGER.info("{} {} | IP: {} | Status: {} | Duration: {}ms{}", 
                method, uri, ip, statusCode, durationMs, slowTag);
    }

    /**
     * 记录 API 访问日志（简化版）
     */
    public static void logApiAccess(String method, String uri, long durationMs) {
        logApiAccess(method, uri, "unknown", 200, durationMs);
    }

    // ==================== 慢查询日志 ====================

    /**
     * 记录慢查询日志
     * 
     * @param sql SQL 语句
     * @param durationMs 执行时间（毫秒）
     */
    public static void logSlowQuery(String sql, long durationMs) {
        SLOW_QUERY_LOGGER.warn("Slow Query Detected | Duration: {}ms | SQL: {}", durationMs, sql);
    }

    /**
     * 记录慢查询日志（带参数）
     * 
     * @param sql SQL 语句
     * @param params 查询参数
     * @param durationMs 执行时间（毫秒）
     */
    public static void logSlowQuery(String sql, Object[] params, long durationMs) {
        SLOW_QUERY_LOGGER.warn("Slow Query Detected | Duration: {}ms | Params: {} | SQL: {}", 
                durationMs, java.util.Arrays.toString(params), sql);
    }

    /**
     * 检查是否为慢查询并记录
     * 
     * @param sql SQL 语句
     * @param durationMs 执行时间（毫秒）
     * @return true-是慢查询，false-正常
     */
    public static boolean checkAndLogSlowQuery(String sql, long durationMs) {
        if (durationMs >= SLOW_QUERY_THRESHOLD_MS) {
            logSlowQuery(sql, durationMs);
            return true;
        }
        return false;
    }

    // ==================== 性能监控日志 ====================

    /**
     * 记录性能监控日志
     * 
     * @param operation 操作名称
     * @param durationMs 执行时间（毫秒）
     */
    public static void logPerformance(String operation, long durationMs) {
        String level = durationMs > PERFORMANCE_WARNING_MS ? "WARN" : "INFO";
        String message = String.format("Operation: %s | Duration: %dms", operation, durationMs);
        
        if (durationMs > PERFORMANCE_WARNING_MS) {
            PERFORMANCE_LOGGER.warn(message);
        } else {
            PERFORMANCE_LOGGER.info(message);
        }
    }

    /**
     * 记录性能监控日志（带详细信息）
     * 
     * @param operation 操作名称
     * @param durationMs 执行时间（毫秒）
     * @param details 详细信息
     */
    public static void logPerformance(String operation, long durationMs, String details) {
        String level = durationMs > PERFORMANCE_WARNING_MS ? "WARN" : "INFO";
        String message = String.format("Operation: %s | Duration: %dms | Details: %s", 
                operation, durationMs, details);
        
        if (durationMs > PERFORMANCE_WARNING_MS) {
            PERFORMANCE_LOGGER.warn(message);
        } else {
            PERFORMANCE_LOGGER.info(message);
        }
    }

    // ==================== 性能计时器 ====================

    /**
     * 启动性能计时器
     * 
     * @param operation 操作名称
     * @return PerformanceTimer 实例
     */
    public static PerformanceTimer startTimer(String operation) {
        return new PerformanceTimer(operation);
    }

    /**
     * 性能计时器内部类
     */
    public static class PerformanceTimer {
        private final String operation;
        private final long startTime;
        private String details;

        public PerformanceTimer(String operation) {
            this.operation = operation;
            this.startTime = System.currentTimeMillis();
        }

        /**
         * 设置详细信息
         */
        public PerformanceTimer withDetails(String details) {
            this.details = details;
            return this;
        }

        /**
         * 停止计时器并记录日志
         * 
         * @return 执行时间（毫秒）
         */
        public long stop() {
            long duration = System.currentTimeMillis() - startTime;
            if (details != null) {
                logPerformance(operation, duration, details);
            } else {
                logPerformance(operation, duration);
            }
            return duration;
        }

        /**
         * 停止计时器但不记录日志
         * 
         * @return 执行时间（毫秒）
         */
        public long stopSilent() {
            return System.currentTimeMillis() - startTime;
        }
    }

    // ==================== 结构化日志 ====================

    /**
     * 记录结构化日志（JSON 格式）
     * 
     * @param event 事件名称
     * @param data 事件数据
     */
    public static void logStructured(String event, Object data) {
        log.info("EVENT: {} | DATA: {}", event, data);
    }

    /**
     * 记录业务操作日志
     * 
     * @param userId 用户 ID
     * @param action 操作动作
     * @param target 操作目标
     * @param result 操作结果
     */
    public static void logBusinessAction(Long userId, String action, String target, String result) {
        log.info("BUSINESS_ACTION | User: {} | Action: {} | Target: {} | Result: {}", 
                userId, action, target, result);
    }

    // ==================== 安全日志 ====================

    /**
     * 记录安全相关事件
     * 
     * @param eventType 事件类型 (LOGIN_FAILED/ACCESS_DENIED/SUSPICIOUS_ACTIVITY)
     * @param userId 用户 ID
     * @param ip IP 地址
     * @param details 详细信息
     */
    public static void logSecurityEvent(String eventType, String userId, String ip, String details) {
        log.warn("SECURITY_EVENT | Type: {} | User: {} | IP: {} | Details: {}", 
                eventType, userId, ip, details);
    }

    /**
     * 记录登录失败
     */
    public static void logLoginFailure(String username, String ip, String reason) {
        logSecurityEvent("LOGIN_FAILED", username, ip, "Reason: " + reason);
    }

    /**
     * 记录权限拒绝
     */
    public static void logAccessDenied(String username, String resource, String ip) {
        logSecurityEvent("ACCESS_DENIED", username, ip, "Resource: " + resource);
    }
}
