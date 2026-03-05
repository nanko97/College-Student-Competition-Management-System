package com.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 
 * 【功能说明】
 * 1. 标记需要记录操作日志的方法
 * 2. 自动记录操作人、操作时间、操作内容等信息
 * 3. 便于后续审计和问题排查
 * 
 * 【使用示例】
 * <pre>
 * {@code
 * @OperationLog("添加竞赛报名")
 * @PostMapping("/add")
 * public R add(@RequestBody JingsaibaomingEntity entity) {
 *     // 业务逻辑
 * }
 * }
 * </pre>
 * 
 * @author 毕业设计优化版
 * @date 2026-03-05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作描述
     * 
     * @return 操作内容的文字描述
     */
    String value() default "";
    
    /**
     * 操作模块
     * 
     * @return 模块名称，如"竞赛管理"、"报名管理"等
     */
    String module() default "系统管理";
    
    /**
     * 是否需要记录请求参数
     * 
     * @return true-记录参数，false-不记录
     */
    boolean recordParams() default true;
}
