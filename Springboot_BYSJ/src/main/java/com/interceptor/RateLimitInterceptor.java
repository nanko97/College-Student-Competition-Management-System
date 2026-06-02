package com.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求限流拦截器
 * 
 * 【论文4.4.5节】设置请求限流，单IP每分钟最多请求60次
 * 采用滑动窗口计数器算法，基于内存存储（线程安全）
 * 生产环境建议替换为 Redis + Lua脚本 实现分布式限流
 *
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // 每分钟最大请求数（从配置文件读取，默认60）
    @Value("${security.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    // 存储每个IP的请求计数和窗口开始时间
    // Map结构: key=IP, value=[当前窗口开始时间戳, 当前窗口请求计数]
    private final Map<String, long[]> counterMap = new ConcurrentHashMap<>();

    // 排除的路径（不需要限流）
    private static final String[] EXCLUDED_PATHS = {
        "/static/", "/upload/", "/admin/", "/front/",
        "/css/", "/js/", "/images/", "/favicon.ico",
        ".html", ".js", ".css", ".png", ".jpg", ".ico"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. OPTIONS请求直接放行（CORS预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 检查是否为排除路径
        String uri = request.getRequestURI();
        for (String excluded : EXCLUDED_PATHS) {
            if (uri.contains(excluded)) {
                return true;
            }
        }

        // 3. 获取客户端IP
        String clientIp = getClientIp(request);

        // 4. 滑动窗口限流算法
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - 60000; // 1分钟窗口

        long[] counter = counterMap.computeIfAbsent(clientIp, k -> new long[]{currentTime, 0});
        
        // 检查窗口是否过期，过期则重置计数
        if (counter[0] < windowStart) {
            counter[0] = currentTime;
            counter[1] = 1;
            return true;
        }

        // 窗口内计数递增
        counter[1]++;

        // 5. 检查是否超过限制
        if (counter[1] > requestsPerMinute) {
            log.warn("【请求限流】IP {} 在1分钟内请求超过{}次，当前{}次，URI：{}",
                    clientIp, requestsPerMinute, counter[1], uri);
            
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(429); // HTTP 429 Too Many Requests
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                writer.print("{\"code\":429,\"msg\":\"请求过于频繁，请稍后再试\"}");
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
            return false;
        }

        // 6. 定期清理过期数据（防止内存泄漏）
        if (counterMap.size() > 1000) {
            counterMap.entrySet().removeIf(entry -> entry.getValue()[0] < windowStart);
        }

        return true;
    }

    /**
     * 获取客户端真实IP地址
     * 支持多层代理场景
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}