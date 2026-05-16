package com.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具
 * 运行此类的main方法生成正确的BCrypt密码
 */
public class PasswordEncryptor {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 admin123 的 BCrypt 加密密码
        String adminPassword = "admin123";
        String hashedAdminPassword = encoder.encode(adminPassword);
        
        System.out.println("========================================");
        System.out.println("密码加密结果");
        System.out.println("========================================");
        System.out.println("原始密码: " + adminPassword);
        System.out.println("BCrypt加密: " + hashedAdminPassword);
        System.out.println("========================================");
        System.out.println("");
        System.out.println("请在数据库中执行以下SQL：");
        System.out.println("UPDATE users SET password = '" + hashedAdminPassword + "' WHERE username = 'admin';");
        System.out.println("");
        System.out.println("========================================");
    }
}
