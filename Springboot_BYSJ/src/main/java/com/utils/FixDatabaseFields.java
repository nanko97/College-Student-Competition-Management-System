package com.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 数据库字段修复工具
 */
public class FixDatabaseFields {
    
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/springbootrd362?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123123";
    
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("✓ 数据库连接成功");
            
            try (Statement stmt = conn.createStatement()) {
                // 为 xuesheng 表添加 role 字段
                try {
                    stmt.execute("ALTER TABLE xuesheng ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '学生' COMMENT '角色'");
                    System.out.println("✓ xuesheng 表 role 字段添加成功");
                } catch (Exception e) {
                    System.out.println("ℹ xuesheng 表 role 字段已存在：" + e.getMessage());
                }
                
                // 为 jiaoshi 表添加 role 字段
                try {
                    stmt.execute("ALTER TABLE jiaoshi ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '教师' COMMENT '角色'");
                    System.out.println("✓ jiaoshi 表 role 字段添加成功");
                } catch (Exception e) {
                    System.out.println("ℹ jiaoshi 表 role 字段已存在：" + e.getMessage());
                }
                
                // 验证字段是否添加成功
                java.sql.ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM xuesheng LIKE 'role'");
                if (rs.next()) {
                    System.out.println("✓ 验证成功：xuesheng.role 字段存在");
                } else {
                    System.out.println("✗ 验证失败：xuesheng.role 字段不存在");
                }
                
                rs = stmt.executeQuery("SHOW COLUMNS FROM jiaoshi LIKE 'role'");
                if (rs.next()) {
                    System.out.println("✓ 验证成功：jiaoshi.role 字段存在");
                } else {
                    System.out.println("✗ 验证失败：jiaoshi.role 字段不存在");
                }
            }
            
            System.out.println("\n✓ 数据库修复完成！请重启应用后重试。");
            
        } catch (Exception e) {
            System.err.println("✗ 数据库修复失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
