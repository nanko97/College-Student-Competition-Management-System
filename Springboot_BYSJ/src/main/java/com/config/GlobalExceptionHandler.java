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

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
@ResponseStatus(HttpStatus.OK)
public class GlobalExceptionHandler {
    
    /**
     * 处理验证异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数验证失败：{}", message);
        return R.error(message);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数绑定失败：{}", message);
        return R.error(message);
    }
    
    /**
     * 处理单个参数验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getMessage();
        log.warn("约束验证失败：{}", message);
        return R.error(message);
    }
    
    /**
     * 处理数据库唯一键冲突
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据库唯一键冲突：", e);
        return R.error("数据已存在，请勿重复添加");
    }
    
    /**
     * 处理 404 错误
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R handler404(NoHandlerFoundException e) {
        log.warn("请求地址不存在：{} {}", e.getRequestURL(), e.getHttpMethod());
        return R.error("接口不存在");
    }
    
    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error("系统异常：", e);
        return R.error("系统繁忙，请稍后再试");
    }
}
