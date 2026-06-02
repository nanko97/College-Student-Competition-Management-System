package com.interceptor;

import com.utils.EnhancedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * API 访问日志拦截器
 */
@Component
public class ApiAccessLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiAccessLogInterceptor.class);

    /** 请求开始时间属性名 */
    private static final String START_TIME_ATTRIBUTE = "startTime";

    /** 慢请求阈值（毫秒） */
    private static final long SLOW_REQUEST_THRESHOLD_MS = 1000;

    /** 需要排除的路径前缀 */
    private static final String[] EXCLUDED_PATHS = {
        "/static/",
        "/css/",
        "/js/",
        "/images/",
        "/favicon.ico",
        "/actuator/health"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // 记录请求信息（DEBUG 级别）
        if (log.isDebugEnabled()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String ip = getClientIp(request);
            
            log.debug("Request Start | Method: {} | URI: {} | Query: {} | IP: {}", 
                    method, uri, queryString, ip);
        }
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) {
        // 此方法在控制器执行后、视图渲染前调用
        // 一般不需要在此处处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        // 跳过排除的路径
        String uri = request.getRequestURI();
        if (isExcludedPath(uri)) {
            return;
        }

        // 计算请求处理时间
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        // 获取请求信息
        String method = request.getMethod();
        int statusCode = response.getStatus();
        String ip = getClientIp(request);

        // 记录 API 访问日志
        EnhancedLogger.logApiAccess(method, uri, ip, statusCode, duration);

        // 记录异常信息
        if (ex != null) {
            log.error("Request Failed | Method: {} | URI: {} | Status: {} | Duration: {}ms | Error: {}", 
                    method, uri, statusCode, duration, ex.getMessage(), ex);
        }

        // 慢请求警告
        if (duration > SLOW_REQUEST_THRESHOLD_MS) {
            log.warn("Slow Request Detected | Method: {} | URI: {} | Duration: {}ms | IP: {}", 
                    method, uri, duration, ip);
        }
    }

    /**
     * 获取客户端真实 IP
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
        
        // 多个代理时，第一个 IP 为真实 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    /**
     * 检查是否为排除的路径
     */
    private boolean isExcludedPath(String uri) {
        if (uri == null) {
            return false;
        }
        
        for (String excludedPath : EXCLUDED_PATHS) {
            if (uri.startsWith(excludedPath)) {
                return true;
            }
        }
        
        return false;
    }
}
