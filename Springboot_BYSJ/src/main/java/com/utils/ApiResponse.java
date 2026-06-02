package com.utils;

import com.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一 API 响应类
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否成功 */
    private boolean success;
    
    /** 状态码 */
    private int code;
    
    /** 响应消息 */
    private String message;
    
    /** 响应数据 */
    private T data;
    
    /** 时间戳 */
    private String timestamp;
    
    /** 请求追踪 ID (可选，用于日志追踪) */
    private String traceId;

    /**
     * 私有构造函数
     */
    private ApiResponse() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // ==================== 成功响应 ====================

    /**
     * 创建成功响应 (无数据)
     */
    public static <T> ApiResponse<T> ok() {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.code = ErrorCode.SUCCESS.getCode();
        response.message = ErrorCode.SUCCESS.getMessage();
        return response;
    }

    /**
     * 创建成功响应 (带消息)
     */
    public static <T> ApiResponse<T> ok(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.code = ErrorCode.SUCCESS.getCode();
        response.message = message;
        return response;
    }

    /**
     * 创建成功响应 (带数据)
     */
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.code = ErrorCode.SUCCESS.getCode();
        response.message = ErrorCode.SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    /**
     * 创建成功响应 (带消息和数据)
     */
    public static <T> ApiResponse<T> ok(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.code = ErrorCode.SUCCESS.getCode();
        response.message = message;
        response.data = data;
        return response;
    }

    // ==================== 失败响应 ====================

    /**
     * 创建失败响应 (默认错误)
     */
    public static <T> ApiResponse<T> error() {
        return error(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 创建失败响应 (带消息)
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.code = ErrorCode.BUSINESS_ERROR.getCode();
        response.message = message;
        return response;
    }

    /**
     * 创建失败响应 (带错误码和消息)
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.code = code;
        response.message = message;
        return response;
    }

    /**
     * 创建失败响应 (使用错误码枚举)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.code = errorCode.getCode();
        response.message = errorCode.getMessage();
        return response;
    }

    /**
     * 创建失败响应 (使用错误码枚举，带详细消息)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.code = errorCode.getCode();
        response.message = message;
        return response;
    }

    // ==================== 认证/授权响应 ====================

    /**
     * 未授权响应
     */
    public static <T> ApiResponse<T> unauthorized() {
        return error(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 未授权响应 (带消息)
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(ErrorCode.UNAUTHORIZED, message);
    }

    /**
     * 禁止访问响应
     */
    public static <T> ApiResponse<T> forbidden() {
        return error(ErrorCode.FORBIDDEN);
    }

    /**
     * 禁止访问响应 (带消息)
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(ErrorCode.FORBIDDEN, message);
    }

    // ==================== 资源相关响应 ====================

    /**
     * 资源不存在响应
     */
    public static <T> ApiResponse<T> notFound() {
        return error(ErrorCode.NOT_FOUND);
    }

    /**
     * 资源不存在响应 (带消息)
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(ErrorCode.NOT_FOUND, message);
    }

    // ==================== 分页响应 ====================

    /**
     * 创建分页响应
     * 
     * @param list 数据列表
     * @param total 总记录数
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     */
    public static <T> ApiResponse<PageData<T>> page(java.util.List<T> list, long total, int pageNum, int pageSize) {
        PageData<T> pageData = new PageData<>();
        pageData.setList(list);
        pageData.setTotal(total);
        pageData.setPageNum(pageNum);
        pageData.setPageSize(pageSize);
        pageData.setPages((int) Math.ceil((double) total / pageSize));
        
        ApiResponse<PageData<T>> response = new ApiResponse<>();
        response.success = true;
        response.code = ErrorCode.SUCCESS.getCode();
        response.message = ErrorCode.SUCCESS.getMessage();
        response.data = pageData;
        return response;
    }

    // ==================== 链式调用 ====================

    /**
     * 设置追踪 ID
     */
    public ApiResponse<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    /**
     * 设置数据
     */
    public ApiResponse<T> withData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 设置消息
     */
    public ApiResponse<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    // ==================== 分页数据内部类 ====================

    /**
     * 分页数据结构
     */
    @Data
    public static class PageData<T> {
        /** 数据列表 */
        private java.util.List<T> list;
        
        /** 总记录数 */
        private long total;
        
        /** 当前页码 */
        private int pageNum;
        
        /** 每页大小 */
        private int pageSize;
        
        /** 总页数 */
        private int pages;
    }
}
