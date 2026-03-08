package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API 文档配置类
 * 
 * 【功能说明】
 * 1. 自动生成 API 接口文档
 * 2. 提供在线 API 测试界面
 * 3. 便于毕业设计答辩演示
 * 
 * 【访问地址】
 * - Swagger UI: http://localhost:8080/springbootrd362/swagger-ui.html
 * - API JSON: http://localhost:8080/springbootrd362/v2/api-docs
 * 
 * 【使用示例】
 * 在 Controller 方法上添加 @ApiOperation 注解：
 * <pre>
 * {@code
 * @ApiOperation("查询竞赛信息")
 * @GetMapping("/{id}")
 * public R get(@PathVariable Long id) {
 *     // ...
 * }
 * }
 * </pre>
 * 
 * @author 毕业设计优化版
 * @date 2026-03-06
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 创建 Docket Bean
     * 
     * @return Docket 配置对象
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描 com.controller 包下的所有 Controller
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                // 扫描所有路径
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建 API 文档基本信息
     * 
     * @return API 文档信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("大学生竞赛管理系统 - API 文档")
                .description("基于 Spring Boot + Vue.js 的竞赛管理系统 API 接口文档")
                .contact(new Contact("毕业设计", "", ""))
                .version("1.0.0")
                .license("Apache License 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }
}
