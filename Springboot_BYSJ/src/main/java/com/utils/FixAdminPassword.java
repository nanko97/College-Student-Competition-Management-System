package com.utils;

import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 管理员密码修复工具
 */
public class FixAdminPassword {
    
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/springbootrd362?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123123";
    
    public static void main(String[] args) {
        try {
            // 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 连接数据库
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ 数据库连接成功");
            
            // 生成 BCrypt 密码
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode("admin123");
            System.out.println("✓ 生成 BCrypt 密码：" + hashedPassword);
            
            // 检查表是否存在
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'users'");
            
            if (!rs.next()) {
                System.out.println("✗ users 表不存在，正在创建...");
                stmt.execute(
                    "CREATE TABLE `users` (" +
                    "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID'," +
                    "`username` varchar(50) NOT NULL COMMENT '用户名'," +
                    "`password` varchar(100) NOT NULL COMMENT '密码 (BCrypt 加密)'," +
                    "`role` varchar(20) DEFAULT '管理员' COMMENT '角色'," +
                    "`addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_username` (`username`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表'"
                );
                System.out.println("✓ users 表创建成功");
            } else {
                System.out.println("✓ users 表已存在");
            }
            
            // 插入或更新管理员账号
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE password = ?, role = ?"
            );
            
            pstmt.setString(1, "admin");
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, "管理员");
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, "管理员");
            
            int rows = pstmt.executeUpdate();
            System.out.println("✓ 管理员账号更新成功，影响行数：" + rows);
            
            // 验证
            rs = stmt.executeQuery("SELECT id, username, role, addtime FROM users WHERE username = 'admin'");
            if (rs.next()) {
                System.out.println("\n===== 管理员账号信息 =====");
                System.out.println("ID: " + rs.getLong("id"));
                System.out.println("用户名：" + rs.getString("username"));
                System.out.println("角色：" + rs.getString("role"));
                System.out.println("创建时间：" + rs.getTimestamp("addtime"));
                System.out.println("==========================");
                System.out.println("\n✓ 登录信息：");
                System.out.println("  账号：admin");
                System.out.println("  密码：admin123");
                System.out.println("  角色：管理员");
            }
            
            // 关闭连接
            rs.close();
            pstmt.close();
            stmt.close();
            conn.close();
            
            System.out.println("\n✓ 所有操作完成！");
            
        } catch (Exception e) {
            System.err.println("✗ 错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
