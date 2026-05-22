package com.config;

import com.interceptor.ApiAccessLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类 - 已禁用
 * 
 * 【说明】
 * 此配置类已被 InterceptorConfig 替代
 * 因为 InterceptorConfig 继承 WebMvcConfigurationSupport，会导致本类的 WebMvcConfigurer 配置失效
 * 所有配置已迁移至 InterceptorConfig
 * 
 * @deprecated 使用 InterceptorConfig 替代
 */
// @Configuration  // 已禁用，避免与 InterceptorConfig 冲突
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ApiAccessLogInterceptor apiAccessLogInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 API 访问日志拦截器
        registry.addInterceptor(apiAccessLogInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/upload/**",     // 【重要】排除上传文件，允许直接访问
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error"
                );
    }

    /**
     * 配置静态资源映射
     * 将 /upload/** 请求映射到项目根目录的 upload 文件夹
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目运行目录（兼容 Jar 包部署）
        String uploadPath = System.getProperty("user.dir") + "/upload/";
        
        // 映射上传文件目录
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
        
        // 映射静态资源目录
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 配置 CORS 跨域支持
     * 允许前端跨域访问 API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8081")  // 允许前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
