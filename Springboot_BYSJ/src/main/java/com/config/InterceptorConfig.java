package com.config;

import com.interceptor.AuthorizationInterceptor;
import com.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;  // 【论文4.4.5节】请求限流拦截器

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 【论文4.4.5节】先注册限流拦截器（在权限校验之前执行）
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/static/**", "/upload/**", "/admin/**", "/front/**",
                "/**/*.html", "/**/*.js", "/**/*.css",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.ico", "/**/*.woff2"
            );
        
        // 注册权限校验拦截器
        registry.addInterceptor(getAuthorizationInterceptor())
            .addPathPatterns("/**")  // 拦截所有路径
            .excludePathPatterns(
                "/static/**",                    // 静态资源
                "/admin/**",                     // 前端页面
                "/front/**",                     // 前台页面
                "/upload/**",                    // 上传文件（作品下载）
                "/users/login",                  // 登录接口
                "/users/register",               // 注册接口
                "/users/resetPass",              // 重置密码
                "/registration/**",              // 统一注册接口
                "/file/download",                // 文件下载接口
                "/zuopin/download",              // 作品下载接口
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
        // 注意：不再调用 super.addInterceptors(registry)，
        // 因为限流拦截器已经单独注册过了
    }

    /**
     * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                // 使用文件路径访问前端资源（实际存在的路径）
                .addResourceLocations("file:./src/main/resources/admin/")
                .addResourceLocations("classpath:/public/");
        // 【重要】单独添加上传文件目录映射，避免覆盖其他静态资源路径
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:./upload/");
        super.addResourceHandlers(registry);
    }

    /**
     * 配置 CORS 跨域支持
     * 注意：由于继承了 WebMvcConfigurationSupport，必须在这里配置，WebMvcConfig 中的配置不会生效
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 修改：允许所有localhost端口（开发环境）
                .allowedOrigins("http://localhost:8081", "http://localhost:8082", "http://127.0.0.1:8081", "http://127.0.0.1:8082")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        super.addCorsMappings(registry);
    }
}
