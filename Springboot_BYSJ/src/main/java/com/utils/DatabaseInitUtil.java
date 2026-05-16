package com.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.*;

/**
 * 数据库初始化工具类
 * 用于创建默认管理员账号
 */
public class DatabaseInitUtil {
    
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/bysj?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    
    /**
     * 初始化数据库表和管理员账号
     */
    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // System.out.println("✓ 数据库连接成功");
            
            // 修复数据库字段（添加 role 字段）
            fixDatabaseFields(conn);
            
            // 创建 users 表
            createUsersTable(conn);
            
            // 创建默认管理员账号
            createAdminAccount(conn);
            
            // System.out.println("✓ 数据库初始化完成！");
            // System.out.println("===============================");
            // System.out.println("管理员账号信息：");
            // System.out.println("  账号：admin");
            // System.out.println("  密码：admin123");
            // System.out.println("  角色：管理员");
            // System.out.println("===============================");
            
        } catch (Exception e) {
            System.err.println("✗ 数据库初始化失败：" + e.getMessage());
        }
    }
    
    private static void fixDatabaseFields(Connection conn) throws SQLException {
        // System.out.println("正在修复数据库字段...");
        
        try (Statement stmt = conn.createStatement()) {
            // 检查 xuesheng 表是否有 role 字段
            ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM xuesheng LIKE 'role'");
            if (!rs.next()) {
                stmt.execute("ALTER TABLE xuesheng ADD COLUMN role VARCHAR(20) DEFAULT '学生' COMMENT '角色'");
            }
            
            // 检查 jiaoshi 表是否有 role 字段
            rs = stmt.executeQuery("SHOW COLUMNS FROM jiaoshi LIKE 'role'");
            if (!rs.next()) {
                stmt.execute("ALTER TABLE jiaoshi ADD COLUMN role VARCHAR(20) DEFAULT '教师' COMMENT '角色'");
            }
        }
        
        // System.out.println("✓ 数据库字段修复完成");
    }
    
    private static void createUsersTable(Connection conn) throws SQLException {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS `users` (" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID'," +
            "  `username` varchar(50) NOT NULL COMMENT '用户名'," +
            "  `password` varchar(100) NOT NULL COMMENT '密码 (BCrypt 加密)'," +
            "  `role` varchar(20) DEFAULT '管理员' COMMENT '角色'," +
            "  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "  PRIMARY KEY (`id`)," +
            "  UNIQUE KEY `uk_username` (`username`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表'";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }
    
    private static void createAdminAccount(Connection conn) throws SQLException {
        // 使用已验证的 BCrypt 密码哈希值（密码：123456）
        String hashedPassword = "***BCRYPT_HASH_REMOVED***";
        
        String checkSQL = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        String insertSQL = "INSERT INTO users (username, password, role) VALUES ('admin', ?, '管理员')";
        String updateSQL = "UPDATE users SET password = ?, role = '管理员' WHERE username = 'admin'";
        
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                updateStmt.setString(1, hashedPassword);
                updateStmt.executeUpdate();
            } else {
                insertStmt.setString(1, hashedPassword);
                insertStmt.executeUpdate();
            }
            
            rs.close();
        }
    }
}
