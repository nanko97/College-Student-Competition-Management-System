package com.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * XSS 防护过滤器
 * 
 * 【功能说明】
 * 1. 拦截所有 HTTP 请求，使用 XssHttpServletRequestWrapper 包装
 * 2. 自动过滤用户输入中的 XSS 攻击代码
 * 3. 保护系统免受跨站脚本攻击
 * 
 * 【使用场景】
 * - 用户表单提交
 * - URL 参数传递
 * - 请求头数据
 * 
 * 【毕业设计说明】
 * - 简化版实现，适合毕业设计要求
 * - 生产环境建议配置更严格的过滤规则
 * 
 * @author 毕业设计优化版
 * @date 2026-03-05
 */
@Component
public class XssFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器配置（本例无需特殊配置）
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // 使用 XSS 包装器包装请求，自动过滤危险内容
            XssHttpServletRequestWrapper xssRequest = 
                new XssHttpServletRequestWrapper(httpRequest);
            
            // 打印日志（可选，便于调试）
            System.out.println("XSS 过滤器已启用，请求 URL: " + httpRequest.getRequestURI());
            
            // 将包装后的请求传递给过滤器链
            chain.doFilter(xssRequest, response);
        } else {
            // 非 HTTP 请求，直接放行
            chain.doFilter(request, response);
        }
    }
    
    @Override
    public void destroy() {
        // 销毁过滤器（本例无需特殊处理）
    }
}
