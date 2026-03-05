package com.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * XSS 过滤请求包装器
 * 
 * 【功能说明】
 * 1. 防止跨站脚本攻击（XSS）
 * 2. 过滤用户输入中的 HTML 标签和 JavaScript 代码
 * 3. 保护系统免受恶意脚本注入
 * 
 * 【毕业设计简化版】
 * - 仅过滤常见的 XSS 攻击向量
 * - 适合毕业设计演示，生产环境建议使用更完善的方案（如 OWASP Java HTML Sanitizer）
 * 
 * @author 毕业设计优化版
 * @date 2026-03-05
 */
public class XssHttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
    
    private HttpServletRequest orgRequest;
    
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.orgRequest = request;
    }
    
    /**
     * 覆盖 getParameter 方法，对参数值进行 XSS 过滤
     * 
     * @param name 参数名
     * @return 过滤后的参数值
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            return cleanXSS(value);
        }
        return value;
    }
    
    /**
     * 覆盖 getHeader 方法，对请求头进行 XSS 过滤
     * 
     * @param name 请求头名称
     * @return 过滤后的请求头值
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value != null && !value.trim().isEmpty()) {
            return cleanXSS(value);
        }
        return value;
    }
    
    /**
     * XSS 过滤核心方法
     * 
     * 【过滤规则】
     * 1. 转义 < > 符号，防止 HTML 标签注入
     * 2. 转义单双引号，防止属性值注入
     * 3. 过滤 javascript: 协议
     * 4. 过滤 on 开头的事件处理器
     * 5. 过滤 eval() 等危险函数
     * 
     * @param value 待过滤的字符串
     * @return 过滤后的字符串
     */
    private String cleanXSS(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        
        // 1. 转义 HTML 特殊字符
        value = value.replaceAll("<", "&lt;")
                     .replaceAll(">", "&gt;");
        
        // 2. 转义单双引号
        value = value.replaceAll("\\(", "&#40;")
                     .replaceAll("\\)", "&#41;")
                     .replaceAll("'", "&#39;")
                     .replaceAll("\"", "&quot;");
        
        // 3. 过滤 javascript: 协议
        value = value.replaceAll("(?i)javascript:", "");
        
        // 4. 过滤常见事件处理器
        value = value.replaceAll("(?i)\\bon\\w+\\s*=", "");
        
        // 5. 过滤 eval() 等危险函数
        value = value.replaceAll("(?i)eval\\((.*)\\)", "");
        value = value.replaceAll("(?i)expression\\((.*)\\)", "");
        
        // 6. 过滤 script 标签（双重保险）
        value = value.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        
        // 7. 过滤 src 表达式
        value = value.replaceAll("(?i)src\\s*=\\s*['\"]?javascript:[^'\"\\s>]*", "");
        
        return value;
    }
    
    /**
     * 获取原始请求对象
     * 
     * @return 原始 HttpServletRequest 对象
     */
    public HttpServletRequest getOrgRequest() {
        return orgRequest;
    }
}
