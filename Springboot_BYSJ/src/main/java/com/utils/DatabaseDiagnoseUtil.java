package com.utils;

import java.sql.*;

/**
 * 数据库诊断工具 - 检查 jingsaixinxi 表结构
 */
public class DatabaseDiagnoseUtil {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/BYSJ_SpringBoot?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123123";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("开始诊断 jingsaixinxi 表...");
        System.out.println("========================================\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL 驱动加载成功\n");

            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✓ 数据库连接成功：" + URL + "\n");

            // 1. 检查表是否存在
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, null, "jingsaixinxi", new String[]{"TABLE"});
            
            if (rs.next()) {
                System.out.println("✓ 表 jingsaixinxi 存在\n");
            } else {
                System.out.println("✗ 表 jingsaixinxi 不存在！");
                System.out.println("请确认数据库是否正确创建\n");
                return;
            }

            // 2. 检查所有字段
            System.out.println("当前表结构：");
            System.out.println("----------------------------------------");
            System.out.printf("%-25s %-20s %-10s%n", "字段名", "类型", "允许 NULL");
            System.out.println("----------------------------------------");
            
            rs = meta.getColumns(null, null, "jingsaixinxi", null);
            
            boolean hasGonghao = false;
            boolean hasJiaoshixingming = false;
            
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("TYPE_NAME") + "(" + rs.getInt("COLUMN_SIZE") + ")";
                int nullableInt = rs.getInt("NULLABLE");
                String nullable = nullableInt == DatabaseMetaData.columnNullable ? "YES" : "NO";
                
                System.out.printf("%-25s %-20s %-10s%n", columnName, columnType, nullable);
                
                if ("gonghao".equals(columnName)) hasGonghao = true;
                if ("jiaoshixingming".equals(columnName)) hasJiaoshixingming = true;
            }
            
            System.out.println("----------------------------------------\n");

            // 3. 诊断结果
            System.out.println("诊断结果：");
            System.out.println("========================================");
            
            if (hasGonghao) {
                System.out.println("✓ gonghao 字段存在");
            } else {
                System.out.println("✗ gonghao 字段不存在 - 需要添加");
            }
            
            if (hasJiaoshixingming) {
                System.out.println("✓ jiaoshixingming 字段存在");
            } else {
                System.out.println("✗ jiaoshixingming 字段不存在 - 需要添加");
            }
            
            System.out.println("========================================\n");

            if (!hasGonghao || !hasJiaoshixingming) {
                System.out.println("建议执行以下 SQL:");
                System.out.println("----------------------------------------");
                if (!hasGonghao) {
                    System.out.println("ALTER TABLE `jingsaixinxi` ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`;");
                }
                if (!hasJiaoshixingming) {
                    System.out.println("ALTER TABLE `jingsaixinxi` ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`;");
                }
                System.out.println("----------------------------------------\n");
            } else {
                System.out.println("✅ 表结构完整，无需修改");
                System.out.println("\n如果仍然无法新增，请检查：");
                System.out.println("1. 后端日志是否有其他错误");
                System.out.println("2. 前端请求数据是否正确");
                System.out.println("3. Token 是否有效");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("✗ 找不到 MySQL 驱动");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ 数据库错误：" + e.getMessage());
            System.err.println("\n可能原因：");
            System.err.println("1. MySQL 服务未启动");
            System.err.println("2. 数据库配置错误（检查 application.yml）");
            System.err.println("3. 数据库 BYSJ_SpringBoot 不存在");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
