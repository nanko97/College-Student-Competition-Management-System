package com.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类（仅用于测试和初始化）
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 admin123 的 BCrypt 哈希
        String hash = encoder.encode("admin123");
        System.out.println("BCrypt Hash for 'admin123': " + hash);
        
        // 验证哈希是否匹配
        boolean matches = encoder.matches("admin123", hash);
        System.out.println("Verification: " + matches);
    }
}
