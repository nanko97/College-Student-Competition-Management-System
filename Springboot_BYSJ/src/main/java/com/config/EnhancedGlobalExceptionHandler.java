package com.config;

import com.exception.BusinessException;
import com.exception.ErrorCode;
import com.utils.ApiResponse;
import com.utils.EnhancedLogger;
import com.utils.R;
import com.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器 - 优化版本
 * 
 * 【功能说明】
 * 1. 统一处理所有异常，返回标准化的响应格式
 * 2. 详细的日志记录，便于问题排查
 * 3. 区分业务异常和系统异常
 * 4. 参数验证异常的友好提示
 * 5. 防止敏感信息泄露
 * 
 * 【异常分类】
 * - 业务异常 (BusinessException): 由业务规则触发，记录 WARN 日志
 * - 参数异常: 由参数验证失败触发，记录 WARN 日志
 * - 系统异常: 由系统错误触发，记录 ERROR 日志
 * 
 * @author 优化版本
 * @date 2026-05-05
 */
@RestControllerAdvice
@Slf4j
public class EnhancedGlobalExceptionHandler {

    // ==================== 业务异常处理 ====================

    /**
     * 处理业务异常
     * 例如：报名已截止、人数已满等业务规则违反
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 | URI: {} | Code: {} | Message: {}", 
                request.getRequestURI(), e.getCode(), e.getMessage());
        
        if (e.getDetails() != null) {
            log.debug("业务异常详情: {}", e.getDetails());
        }
        
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    // ==================== 参数验证异常处理 ====================

    /**
     * 处理 @Valid 注解触发的参数验证异常
     * 例如：注册时用户名或密码为空
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数验证失败 | URI: {} | Message: {}", request.getRequestURI(), message);
        
        return ApiResponse.error(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理参数绑定异常
     * 例如：前端传递的参数格式不正确
     */
    @ExceptionHandler(BindException.class)
    public ApiResponse<?> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数绑定失败 | URI: {} | Message: {}", request.getRequestURI(), message);
        
        return ApiResponse.error(ErrorCode.PARAM_INVALID, message);
    }

    /**
     * 处理单个参数验证异常 (@ConstraintViolation)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<?> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("约束验证失败 | URI: {} | Message: {}", request.getRequestURI(), message);
        
        return ApiResponse.error(ErrorCode.PARAM_INVALID, message);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        
        String message = "缺少必要参数: " + e.getParameterName();
        log.warn("缺少请求参数 | URI: {} | Parameter: {}", request.getRequestURI(), e.getParameterName());
        
        return ApiResponse.error(ErrorCode.PARAM_MISSING, message);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        
        String message = String.format("参数 '%s' 的值 '%s' 格式不正确，期望类型: %s",
                e.getName(), e.getValue(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        
        log.warn("参数类型不匹配 | URI: {} | Message: {}", request.getRequestURI(), message);
        
        return ApiResponse.error(ErrorCode.PARAM_INVALID, message);
    }

    // ==================== 数据库异常处理 ====================

    /**
     * 处理数据库唯一键冲突
     * 例如：用户名已存在、学号重复等
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<?> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.warn("数据库唯一键冲突 | URI: {}", request.getRequestURI());
        log.debug("详细错误信息", e);
        
        return ApiResponse.error(ErrorCode.RESOURCE_ALREADY_EXISTS, "数据已存在，请勿重复添加");
    }

    /**
     * 处理 SQL 异常
     * 例如：数据库连接失败、SQL 语法错误等
     */
    @ExceptionHandler(SQLException.class)
    public ApiResponse<?> handleSQLException(SQLException e, HttpServletRequest request) {
        log.error("SQL 异常 | URI: {} | Error Code: {} | Message: {}", 
                request.getRequestURI(), e.getErrorCode(), e.getMessage(), e);
        
        // 记录性能日志
        EnhancedLogger.logPerformance("SQL_ERROR", 0, 
                String.format("URI: %s, ErrorCode: %d", request.getRequestURI(), e.getErrorCode()));
        
        return ApiResponse.error(ErrorCode.DATABASE_ERROR, "数据库操作失败，请稍后再试");
    }

    // ==================== 认证/授权异常处理 ====================

    /**
     * 处理运行时异常（兜底）
     * 例如：空指针、数组越界等程序错误
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常 | URI: {} | Exception: {}", 
                request.getRequestURI(), e.getClass().getName(), e);
        
        return ApiResponse.error(ErrorCode.SYSTEM_ERROR, "系统运行错误，请稍后再试");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ApiResponse<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常 | URI: {} | Message: {}", request.getRequestURI(), e.getMessage(), e);
        
        return ApiResponse.error(ErrorCode.SYSTEM_ERROR, "系统内部错误，请联系开发人员");
    }

    // ==================== Web 异常处理 ====================

    /**
     * 处理 404 错误
     * 例如：访问了不存在的接口
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("404 错误 | Method: {} | URI: {}", e.getHttpMethod(), e.getRequestURL());
        
        return ApiResponse.notFound("接口不存在，请检查请求地址是否正确");
    }

    /**
     * 处理不支持的请求方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<?> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        
        log.warn("不支持的请求方法 | URI: {} | Method: {}", request.getRequestURI(), e.getMethod());
        
        return ApiResponse.error(ErrorCode.PARAM_INVALID, 
                "不支持的请求方法，请使用：" + e.getSupportedHttpMethods());
    }

    /**
     * 处理不支持的媒体类型异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResponse<?> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        
        log.warn("不支持的媒体类型 | URI: {} | ContentType: {}", request.getRequestURI(), e.getContentType());
        
        return ApiResponse.error(ErrorCode.PARAM_INVALID, "不支持的内容类型，请使用：application/json");
    }

    // ==================== 文件上传异常处理 ====================

    /**
     * 处理文件上传大小超出限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("文件上传大小超出限制: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.PARAM_INVALID, "上传文件过大，请压缩后重新上传（最大 50MB）");
    }

    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMultipartException(MultipartException e) {
        log.error("文件上传异常", e);
        return ApiResponse.error(ErrorCode.SYSTEM_ERROR, "文件上传失败，请重试");
    }

    // ==================== 数据库异常处理增强 ====================

    /**
     * 处理数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error("数据库访问异常 | URI: {}", request.getRequestURI(), e);
        return ApiResponse.error(ErrorCode.DATABASE_ERROR, "数据库操作失败，请联系管理员");
    }

    // ==================== 通用异常处理 ====================

    /**
     * 处理所有其他未分类的异常
     * 这是最后一道防线，确保任何异常都不会暴露详细信息给用户
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 | URI: {} | Exception: {} | Message: {}", 
                request.getRequestURI(), e.getClass().getName(), e.getMessage(), e);
        
        return ApiResponse.error(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后再试");
    }
}
