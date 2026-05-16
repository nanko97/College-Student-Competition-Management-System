package com.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码重置工具 - 生成123456的BCrypt加密密码
 * 运行方式: 右键 -> Run 'PasswordResetUtil.main()'
 */
public class PasswordResetUtil {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 123456 的BCrypt加密密码
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("===========================================");
        System.out.println("  密码重置工具 - 统一密码: 123456");
        System.out.println("===========================================\n");
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后密码: " + encodedPassword);
        System.out.println("\n===========================================");
        System.out.println("  SQL 更新语句");
        System.out.println("===========================================\n");
        
        // 生成SQL更新语句
        System.out.println("-- 1. 更新管理员密码 (users表)");
        System.out.println("UPDATE users SET password = '" + encodedPassword + "';\n");
        
        System.out.println("-- 2. 更新学生密码 (xuesheng表)");
        System.out.println("UPDATE xuesheng SET mima = '" + encodedPassword + "';\n");
        
        System.out.println("-- 3. 更新教师密码 (jiaoshi表)");
        System.out.println("UPDATE jiaoshi SET mima = '" + encodedPassword + "';\n");
        
        System.out.println("===========================================");
        System.out.println("  执行方式");
        System.out.println("===========================================");
        System.out.println("1. 复制上面的SQL语句");
        System.out.println("2. 在MySQL客户端或Navicat中执行");
        System.out.println("3. 或在 application.yml 同目录创建 reset-password.sql 文件");
        System.out.println("===========================================");
    }
}
