package com.exception;

import lombok.Getter;

/**
 * 错误码枚举 - 产品化版本
 * 
 * 【功能说明】
 * 统一定义系统中所有可能的错误码，便于：
 * 1. 前端根据错误码进行特定处理
 * 2. 国际化支持
 * 3. 错误统计和分析
 * 
 * 【错误码规范】
 * - 1xxx: 参数相关错误
 * - 2xxx: 认证相关错误
 * - 3xxx: 权限相关错误
 * - 4xxx: 资源相关错误
 * - 5xxx: 业务逻辑错误
 * - 6xxx: 系统相关错误
 * 
 * @author 产品化优化版本
 * @date 2026-04-06
 */
@Getter
public enum ErrorCode {

    // ==================== 成功 ====================
    SUCCESS(200, "操作成功"),

    // ==================== 参数错误 (1xxx) ====================
    PARAM_ERROR(1001, "参数错误"),
    PARAM_MISSING(1002, "缺少必要参数"),
    PARAM_INVALID(1003, "参数格式不正确"),
    PARAM_OUT_OF_RANGE(1004, "参数值超出范围"),

    // ==================== 认证错误 (2xxx) ====================
    UNAUTHORIZED(2001, "未授权访问"),
    TOKEN_EXPIRED(2002, "令牌已过期"),
    TOKEN_INVALID(2003, "令牌无效"),
    LOGIN_FAILED(2004, "登录失败，用户名或密码错误"),
    ACCOUNT_LOCKED(2005, "账号已被锁定"),
    ACCOUNT_DISABLED(2006, "账号已被禁用"),

    // ==================== 权限错误 (3xxx) ====================
    FORBIDDEN(3001, "没有权限执行此操作"),
    ROLE_NOT_ALLOWED(3002, "当前角色不允许执行此操作"),

    // ==================== 资源错误 (4xxx) ====================
    NOT_FOUND(4001, "资源不存在"),
    RESOURCE_ALREADY_EXISTS(4002, "资源已存在"),
    RESOURCE_DELETED(4003, "资源已被删除"),

    // ==================== 业务错误 (5xxx) ====================
    BUSINESS_ERROR(5001, "业务处理失败"),
    
    // 竞赛相关业务错误
    JINGSAI_NOT_FOUND(5101, "竞赛不存在"),
    JINGSAI_ENDED(5102, "竞赛已结束"),
    JINGSAI_NOT_STARTED(5103, "竞赛尚未开始"),
    BAOMING_DEADLINE_PASSED(5104, "报名已截止"),
    BAOMING_COUNT_EXCEEDED(5105, "报名人数已达上限"),
    BAOMING_DUPLICATE(5106, "请勿重复报名"),
    BAOMING_NOT_FOUND(5107, "报名信息不存在"),
    
    // 用户相关业务错误
    USER_NOT_FOUND(5201, "用户不存在"),
    USER_ALREADY_EXISTS(5202, "用户已存在"),
    PASSWORD_INCORRECT(5203, "密码错误"),
    OLD_PASSWORD_INCORRECT(5204, "原密码错误"),
    
    // 文件相关业务错误
    FILE_UPLOAD_FAILED(5301, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(5302, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(5303, "文件大小超出限制"),
    FILE_NOT_FOUND(5304, "文件不存在"),

    // ==================== 系统错误 (6xxx) ====================
    SYSTEM_ERROR(6001, "系统内部错误"),
    DATABASE_ERROR(6002, "数据库操作失败"),
    REDIS_ERROR(6003, "缓存服务异常"),
    EXTERNAL_SERVICE_ERROR(6004, "外部服务调用失败"),
    SERVICE_UNAVAILABLE(6005, "服务暂时不可用");

    /** 错误码 */
    private final int code;
    
    /** 错误消息 */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     * 
     * @param code 错误码
     * @return 对应的 ErrorCode 枚举，未找到返回 null
     */
    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
