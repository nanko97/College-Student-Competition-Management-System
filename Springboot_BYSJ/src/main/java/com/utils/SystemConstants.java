package com.utils;

/**
 * 系统常量定义
 * 避免魔法数字，提升代码可维护性
 */
public class SystemConstants {

    // ==================== 审核状态常量 ====================
    
    /** 审核通过 */
    public static final String STATUS_PASS = "通过";
    
    /** 审核不通过 */
    public static final String STATUS_REJECT = "不通过";
    
    /** 待审核 */
    public static final String STATUS_PENDING = "待审核";

    // ==================== 支付状态常量 ====================
    
    /** 已缴费 */
    public static final String PAY_STATUS_PAID = "已缴费";
    
    /** 未缴费 */
    public static final String PAY_STATUS_UNPAID = "未缴费";

    // ==================== 团队角色常量 ====================
    
    /** 团队负责人 */
    public static final String TEAM_ROLE_LEADER = "负责人";
    
    /** 团队成员 */
    public static final String TEAM_ROLE_MEMBER = "队员";

    // ==================== 文件上传常量 ====================
    
    /** 作品文件最大大小（50MB） */
    public static final long MAX_ZUOPIN_FILE_SIZE = 50 * 1024 * 1024;
    
    /** 凭证文件最大大小（5MB） */
    public static final long MAX_PINGZHENG_FILE_SIZE = 5 * 1024 * 1024;
    
    /** 作品文件允许的后缀 */
    public static final String[] ALLOWED_ZUOPIN_EXTS = {
        "doc", "docx", "pdf", "zip", "rar", "7z", "ppt", "pptx", "xls", "xlsx"
    };
    
    /** 图片文件允许的后缀 */
    public static final String[] ALLOWED_IMAGE_EXTS = {
        "jpg", "jpeg", "png", "gif", "bmp", "webp"
    };

    // ==================== HTTP状态码常量 ====================
    
    /** 成功 */
    public static final int HTTP_SUCCESS = 0;
    
    /** 未授权 */
    public static final int HTTP_UNAUTHORIZED = 401;
    
    /** 权限不足 */
    public static final int HTTP_FORBIDDEN = 403;
    
    /** 未找到 */
    public static final int HTTP_NOT_FOUND = 404;
    
    /** 服务器错误 */
    public static final int HTTP_INTERNAL_ERROR = 500;

    // ==================== 用户角色常量 ====================
    
    /** 学生角色 */
    public static final String ROLE_STUDENT = "学生";
    
    /** 教师角色 */
    public static final String ROLE_TEACHER = "教师";
    
    /** 管理员角色 */
    public static final String ROLE_ADMIN = "管理员";
    
    /** 学生角色表名 */
    public static final String TABLE_STUDENT = "xuesheng";
    
    /** 教师角色表名 */
    public static final String TABLE_TEACHER = "jiaoshi";
    
    /** 管理员角色表名 */
    public static final String TABLE_ADMIN = "users";

    // ==================== 默认值常量 ====================
    
    /** 默认密码 */
    public static final String DEFAULT_PASSWORD = "123456";
    
    /** 分页默认页码 */
    public static final int DEFAULT_PAGE = 1;
    
    /** 分页默认大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    // ==================== 登录相关常量 ====================
    
    /** 最大登录失败次数 */
    public static final int MAX_LOGIN_FAIL_COUNT = 5;
    
    /** Token请求头名称 */
    public static final String TOKEN_HEADER_NAME = "Token";
    
    /** Session用户ID键 */
    public static final String SESSION_USER_ID = "userId";
    
    /** Session角色键 */
    public static final String SESSION_ROLE = "role";
    
    /** Session表名键 */
    public static final String SESSION_TABLE_NAME = "tableName";
    
    /** Session用户名键 */
    public static final String SESSION_USERNAME = "username";

    // ==================== 文件路径常量 ====================
    
    /** 作品上传目录 */
    public static final String UPLOAD_ZUOPIN_PATH = "/upload/zuopin/";
    
    /** 凭证上传目录 */
    public static final String UPLOAD_PINGZHENG_PATH = "/upload/pingzheng/";
    
    /** 通用上传目录 */
    public static final String UPLOAD_COMMON_PATH = "/upload/";

    // ==================== 变更类型常量 ====================
    
    /** 添加成员 */
    public static final String CHANGE_TYPE_ADD = "添加成员";
    
    /** 移除成员 */
    public static final String CHANGE_TYPE_REMOVE = "移除成员";
    
    /** 更换负责人 */
    public static final String CHANGE_TYPE_CHANGE_LEADER = "更换负责人";

    // ==================== 复核状态常量 ====================
    
    /** 复核已通过 */
    public static final String FUHE_STATUS_PASS = "已通过";
    
    /** 复核已驳回 */
    public static final String FUHE_STATUS_REJECT = "已驳回";
    
    /** 复核待审核 */
    public static final String FUHE_STATUS_PENDING = "待审核";

    // ==================== 私有构造函数 ====================
    /**
     * 私有构造函数，防止实例化
     */
    private SystemConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
