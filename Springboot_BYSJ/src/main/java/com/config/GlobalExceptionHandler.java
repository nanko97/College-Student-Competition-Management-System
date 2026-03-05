package com.config;

import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

/**
 * 全局异常处理器
 * 
 * 【毕业设计优化版】
 * 作用：统一处理系统中所有异常，返回友好的错误提示给用户
 * 
 * 使用说明：
 * - 所有异常都会被这里捕获并记录到日志
 * - 用户看到的是简化后的友好提示
 * - 开发者可以通过日志查看详细错误信息
 */
@RestControllerAdvice
@Slf4j
@ResponseStatus(HttpStatus.OK)
public class GlobalExceptionHandler {
    
    /**
     * 处理参数验证异常（@Valid 注解触发的验证）
     * 例如：注册时用户名或密码为空
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数验证失败：{}", message);
        return R.error("输入参数有误：" + message);
    }
    
    /**
     * 处理参数绑定异常
     * 例如：前端传递的参数格式不正确
     */
    @ExceptionHandler(BindException.class)
    public R handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数绑定失败：{}", message);
        return R.error("参数格式错误：" + message);
    }
    
    /**
     * 处理单个参数验证异常
     * 例如：某个字段的值不符合要求
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getMessage();
        log.warn("约束验证失败：{}", message);
        return R.error("数据验证失败：" + message);
    }
    
    /**
     * 处理数据库唯一键冲突
     * 例如：用户名已存在、学号重复等
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据库唯一键冲突", e);
        return R.error("数据已存在，请勿重复添加");
    }
    
    /**
     * 处理 SQL 异常
     * 例如：数据库连接失败、SQL 语法错误等
     */
    @ExceptionHandler(SQLException.class)
    public R handleSQLException(SQLException e) {
        log.error("SQL 异常：{}", e.getMessage());
        return R.error("数据库操作失败，请检查数据库连接或联系管理员");
    }
    
    /**
     * 处理运行时异常
     * 例如：空指针、数组越界等程序错误
     */
    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return R.error("系统运行错误，请稍后再试或联系管理员");
    }
    
    /**
     * 处理空指针异常
     * 例如：访问了 null 对象的属性
     */
    @ExceptionHandler(NullPointerException.class)
    public R handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return R.error("系统内部错误，数据访问异常，请联系开发人员");
    }
    
    /**
     * 处理 404 错误
     * 例如：访问了不存在的接口
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R handler404(NoHandlerFoundException e) {
        log.warn("请求地址不存在：{} {}", e.getRequestURL(), e.getHttpMethod());
        return R.error("接口不存在，请检查请求地址是否正确");
    }
    
    /**
     * 处理所有其他未分类的异常
     * 这是最后一道防线，确保任何异常都不会暴露详细信息给用户
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error("系统异常", e);
        return R.error("系统繁忙，请稍后再试");
    }
}
