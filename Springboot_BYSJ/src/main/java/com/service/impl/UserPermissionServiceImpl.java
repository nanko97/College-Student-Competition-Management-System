package com.service.impl;

import com.service.UserPermissionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户权限服务实现
 */
@Service
public class UserPermissionServiceImpl implements UserPermissionService {
    
    // 学生允许访问的接口前缀
    private static final Set<String> STUDENT_PERMISSIONS = new HashSet<>(Arrays.asList(
        // 用户信息（登录后获取）
        "/users/info",
        // 竞赛相关
        "/jingsaixinxi/list",
        "/jingsaixinxi/detail",
        "/jingsaixinxi/page",
        "/jingsaixinxi/query",
        "/jingsaixinxi/info",
        // 报名相关
        "/jingsaibaoming/add",
        "/jingsaibaoming/save",  // 添加报名保存接口权限
        "/jingsaibaoming/my",
        "/jingsaibaoming/list",
        "/jingsaibaoming/page",
        "/jingsaibaoming/info",
        "/jingsaibaoming/query",
        "/jingsaibaoming/update",
        "/jingsaibaoming/delete",
        // 作品评分相关
        "/zuopindafen/list",
        "/zuopindafen/page",
        "/zuopindafen/info",
        "/zuopindafen/query",
        "/zuopindafen/my",
        // 成绩复核相关（学生提交复核申请）
        "/zuopindafenfuhe/add",
        "/zuopindafenfuhe/save",
        "/zuopindafenfuhe/my",
        "/zuopindafenfuhe/list",
        "/zuopindafenfuhe/page",
        "/zuopindafenfuhe/info",
        // 学生自己的信息
        "/xuesheng/info",
        "/xuesheng/update",
        "/xuesheng/session",
        "/xuesheng/resetPass",
        // 文件下载
        "/file/download",
        "/file/upload",
        // 系统配置
        "/config/list",
        "/common/*",
        // 学生我的缴费记录
        "/jingsai/jiaofei/my/list",
        "/jingsai/jiaofei/my/page",
        // 学生我的晋级记录
        "/jingsai/jinji/my/list",
        "/jingsai/jinji/my/page",
        // 学生提交缴费凭证
        "/jingsai/jiaofei/submit",
        // 学生晋级申请
        "/jingsai/jinji/apply",
        // 学生作品管理
        "/zuopin/my/list",
        "/zuopin/my/page",
        "/zuopin/upload",
        "/zuopin/delete",
        "/zuopin/statistics",
        // 团队管理相关（学生）
        "/jingsai/tuandui/list",  // 浏览所有团队（申请加入）
        "/jingsai/tuandui/my/list",  // 查询我的团队
        "/jingsai/tuandui/myStatus",  // 查询我的团队状态
        "/jingsai/tuandui/chengyuan/list",  // 查看团队成员
        "/jingsai/tuandui/chengyuan/remove",  // 移除成员（负责人）
        "/jingsai/tuandui/create",  // 创建团队
        "/jingsai/tuandui/update",  // 更新团队信息
        "/jingsai/tuandui/dissolve",  // 解散团队（负责人）
        "/jingsai/tuandui/info",  // 查询团队详情
        // 团队申请相关
        "/tuandui/apply/join",  // 申请加入
        "/tuandui/apply/quit",  // 申请退出
        "/tuandui/apply/my/list",  // 我的申请记录
        "/tuandui/apply/shenhe/list"  // 审核列表（负责人）
    ));
    
    // 教师允许访问的接口前缀（通配符模式）
    private static final String[] TEACHER_PERMISSION_PATTERNS = {
        // 用户信息（登录后获取）
        "/users/info",
        "/jingsaixinxi/",
        "/jingsaibaoming/",
        "/zuopindafen/",
        "/zuopindafenfuhe/",  // 成绩复核相关接口
        "/xuesheng/list",
        "/xuesheng/info",
        "/jiaoshi/",
        "/file/",
        // 缴费相关
        "/jingsai/jiaofei/",
        // 晋级相关
        "/jingsai/jinji/",
        // 级别版本
        "/jingsai/jibie/",
        // 作品管理
        "/zuopin/",
        // 赛道管理
        "/jingsai/saidao/",
        // 团队管理
        "/jingsai/tuandui/",
        // 人员变更审核
        "/jingsai/biangueng/",
        // 竞赛费用配置
        "/jingsai/feiyong/"
    };
    
    @Override
    public boolean hasPermission(String role, String uri) {
        // 移除上下文路径
        String path = uri.replace("/BYSJ_Springboot", "");
        
        // 支持中英文角色名映射
        String normalizedRole = normalizeRole(role);
        
        switch (normalizedRole) {
            case "管理员":
            case "admin":
                return true; // 管理员拥有所有权限
                
            case "学生":
            case "xuesheng":
                return checkStudentPermission(path);
                
            case "教师":
            case "jiaoshi":
                return checkTeacherPermission(path);
                
            default:
                return false;
        }
    }
    
    /**
     * 角色名称规范化（支持中英文）
     */
    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }
        
        // 英文转中文映射
        switch (role.toLowerCase()) {
            case "admin":
                return "管理员";
            case "xuesheng":
                return "学生";
            case "jiaoshi":
                return "教师";
            default:
                return role;
        }
    }
    
    /**
     * 检查学生权限
     */
    private boolean checkStudentPermission(String uri) {
        // 精确匹配
        for (String permission : STUDENT_PERMISSIONS) {
            if (permission.endsWith("*")) {
                // 通配符匹配
                String prefix = permission.substring(0, permission.length() - 1);
                if (uri.startsWith(prefix)) {
                    return true;
                }
            } else {
                // 精确匹配
                if (uri.equals(permission)) {
                    return true;
                }
                // 前缀匹配（用于 /info/{id} 这样的路径）
                if (uri.startsWith(permission + "/")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 检查教师权限
     */
    private boolean checkTeacherPermission(String uri) {
        for (String pattern : TEACHER_PERMISSION_PATTERNS) {
            if (pattern.endsWith("*")) {
                // 通配符匹配
                String prefix = pattern.substring(0, pattern.length() - 1);
                if (uri.startsWith(prefix)) {
                    return true;
                }
            } else {
                // 前缀匹配
                if (uri.startsWith(pattern)) {
                    return true;
                }
            }
        }
        return false;
    }
}
