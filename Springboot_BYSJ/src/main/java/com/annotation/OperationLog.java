package com.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
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
