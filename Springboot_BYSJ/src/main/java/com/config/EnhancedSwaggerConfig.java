package com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 增强 Swagger API 文档配置类 - 产品化版本
 * 
 * 【功能说明】
 * 1. 自动生成 API 接口文档
 * 2. 提供在线 API 测试界面
 * 3. 支持 JWT Token 认证
 * 4. 分组管理 API 接口
 * 5. 自定义文档信息
 * 
 * 【访问地址】
 * - Swagger UI: http://localhost:9090/BYSJ_Springboot/swagger-ui.html
 * - API JSON: http://localhost:9090/BYSJ_Springboot/v2/api-docs
 * 
 * 【常用注解】
 * <pre>
 * {@code
 * // Controller 类上
 * @Api(tags = "竞赛管理")
 * 
 * // 方法上
 * @ApiOperation("查询竞赛列表")
 * @ApiImplicitParams({
 *     @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1"),
 *     @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10")
 * })
 * 
 * // 实体类上
 * @ApiModel("竞赛信息")
 * public class Jingsai {
 *     @ApiModelProperty(value = "竞赛ID", example = "1")
 *     private Long id;
 * }
 * }
 * </pre>
 * 
 * @author 产品化优化版本
 * @date 2026-04-06
 */
@Configuration
@EnableSwagger2
public class EnhancedSwaggerConfig {

    @Value("${swagger.enabled:true}")
    private boolean swaggerEnabled;

    /**
     * 创建 Docket Bean - 主 API
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .groupName("全部接口");
    }

    /**
     * 用户管理 API 分组
     */
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("用户管理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.ant("/user/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 竞赛管理 API 分组
     */
    @Bean
    public Docket jingsaiApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("竞赛管理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.ant("/jingsai/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 报名管理 API 分组
     */
    @Bean
    public Docket baomingApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("报名管理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.ant("/baoming/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 系统管理 API 分组
     */
    @Bean
    public Docket systemApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("系统管理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.regex("/config/.*|/file/.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 缴费管理 API 分组
     */
    @Bean
    public Docket jiaofeiApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("缴费管理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.ant("/jingsai/jiaofei/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 晋级管理 API 分组
     */
    @Bean
    public Docket jinjiApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("晋级管理")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.ant("/jingsai/jinji/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 级别认定 API 分组
     */
    @Bean
    public Docket jibieApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("级别认定")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.ant("/jingsai/jibie/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 赛道团队 API 分组
     */
    @Bean
    public Docket saidaoTuanduiApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("赛道团队")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.regex("/jingsai/saidao/.*|/jingsai/tuandui/.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 人员变更 API 分组
     */
    @Bean
    public Docket bianguengApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnabled)
                .groupName("人员变更")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.ant("/jingsai/biangueng/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 构建 API 文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("大学生竞赛管理系统 - API 文档")
                .description("基于 Spring Boot + Vue.js 的竞赛管理系统 API 接口文档\n\n" +
                        "## 功能模块\n" +
                        "- 用户管理：注册、登录、信息管理\n" +
                        "- 竞赛管理：发布、查询、更新竞赛\n" +
                        "- 报名管理：在线报名、审核、统计\n" +
                        "- 缴费管理：费用配置、缴费凭证、审核缴费\n" +
                        "- 晋级管理：晋级关系、发起晋级、审核晋级\n" +
                        "- 级别认定：级别版本管理、历史追溯\n" +
                        "- 赛道团队：赛道配置、团队管理、成员管理\n" +
                        "- 人员变更：晋级后人员调整、变更日志\n" +
                        "- 作品管理：作品提交、评分、排名\n" +
                        "- 系统管理：配置、文件、日志\n\n" +
                        "## 认证方式\n" +
                        "大部分接口需要 JWT Token 认证，请在请求头中添加：\n" +
                        "```\n" +
                        "Authorization: Bearer {your-token}\n" +
                        "```")
                .contact(new Contact("大学生竞赛管理系统", "", ""))
                .version("4.0.0-ENHANCED")
                .license("Apache License 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .build();
    }

    /**
     * 配置安全方案 (JWT)
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> schemes = new ArrayList<>();
        schemes.add(new ApiKey("Authorization", "Authorization", "header"));
        return schemes;
    }

    /**
     * 配置安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> contexts = new ArrayList<>();
        contexts.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!auth|register).*$"))
                .build());
        return contexts;
    }

    /**
     * 默认授权引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }
}
