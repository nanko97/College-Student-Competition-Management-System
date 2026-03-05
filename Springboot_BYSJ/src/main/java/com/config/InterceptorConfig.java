package com.config;

import com.interceptor.AuthorizationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthorizationInterceptor())
            .addPathPatterns("/**")  // 拦截所有路径
            .excludePathPatterns(
                "/static/**",                    // 静态资源
                "/admin/**",                     // 前端页面
                "/front/**",                     // 前台页面
                "/users/login",                  // 登录接口
                "/users/register",               // 注册接口
                "/users/resetPass",              // 重置密码
                "/registration/**",              // 统一注册接口
                "/file/download",                // 文件下载
                "/jingsaixinxi/list",            // 竞赛列表（公开）
                "/jingsaixinxi/page",            // 竞赛分页（公开）
                "/jingsaixinxi/detail/**",       // 竞赛详情（公开）
                "/**/*.html",                    // HTML 文件
                "/**/*.js",                      // JS 文件
                "/**/*.css",                     // CSS 文件
                "/**/*.png",                     // PNG 图片
                "/**/*.jpg",                     // JPG 图片
                "/**/*.jpeg",                    // JPEG 图片
                "/**/*.gif",                     // GIF 图片
                "/**/*.ico",                     // ICO 图标
                "/**/*.woff2",                   // 字体文件
                "/actuator/**"                   // 监控端点
            );
        super.addInterceptors(registry);
    }

    /**
     * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/admin/")
                .addResourceLocations("classpath:/front/")
                .addResourceLocations("classpath:/public/");
        super.addResourceHandlers(registry);
    }
}
