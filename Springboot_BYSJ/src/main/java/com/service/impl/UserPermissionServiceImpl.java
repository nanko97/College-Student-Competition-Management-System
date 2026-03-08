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
        "/common/*"
    ));
    
    // 教师允许访问的接口前缀（通配符模式）
    private static final String[] TEACHER_PERMISSION_PATTERNS = {
        "/jingsaixinxi/",
        "/jingsaibaoming/",
        "/zuopindafen/",
        "/xuesheng/list",
        "/xuesheng/info",
        "/jiaoshi/",
        "/file/"
    };
    
    @Override
    public boolean hasPermission(String role, String uri) {
        // 移除上下文路径
        String path = uri.replace("/springbootrd362", "");
        
        switch (role) {
            case "管理员":
                return true; // 管理员拥有所有权限
                
            case "学生":
                return checkStudentPermission(path);
                
            case "教师":
                return checkTeacherPermission(path);
                
            default:
                return false;
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
