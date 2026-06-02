package com.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final int code;
    
    /** 错误信息 */
    private final String message;
    
    /** 详细信息（可选） */
    private final String details;

    /**
     * 构造函数 - 简单消息
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.BUSINESS_ERROR.getCode();
        this.message = message;
        this.details = null;
    }

    /**
     * 构造函数 - 带错误码
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.details = null;
    }

    /**
     * 构造函数 - 使用预定义错误码
     * 
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.details = null;
    }

    /**
     * 构造函数 - 带详细信息
     * 
     * @param message 错误消息
     * @param details 详细信息
     */
    public BusinessException(String message, String details) {
        super(message);
        this.code = ErrorCode.BUSINESS_ERROR.getCode();
        this.message = message;
        this.details = details;
    }

    /**
     * 构造函数 - 带原因
     * 
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCode.BUSINESS_ERROR.getCode();
        this.message = message;
        this.details = cause != null ? cause.getMessage() : null;
    }

    /**
     * 构造函数 - 完整参数
     * 
     * @param errorCode 错误码枚举
     * @param details 详细信息
     * @param cause 原始异常
     */
    public BusinessException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.details = details;
    }
}
