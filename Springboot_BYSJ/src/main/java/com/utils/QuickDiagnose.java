package com.utils;

import java.sql.*;

/**
 * 快速诊断工具 - 只输出关键信息
 */
public class QuickDiagnose {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/BYSJ_SpringBoot?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123123";

    public static void main(String[] args) {
        System.out.println("=== jingsaixinxi 表字段检查 ===\n");

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "jingsaixinxi", null);
            
            boolean hasGonghao = false;
            boolean hasJiaoshixingming = false;
            int totalColumns = 0;
            
            System.out.println("所有字段列表：");
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                totalColumns++;
                System.out.println("  " + totalColumns + ". " + colName);
                
                if ("gonghao".equals(colName)) hasGonghao = true;
                if ("jiaoshixingming".equals(colName)) hasJiaoshixingming = true;
            }
            
            System.out.println("\n共计：" + totalColumns + " 个字段\n");
            System.out.println("关键字段检查：");
            System.out.println("  gonghao: " + (hasGonghao ? "✓ 存在" : "✗ 不存在"));
            System.out.println("  jiaoshixingming: " + (hasJiaoshixingming ? "✓ 存在" : "✗ 不存在"));
            
            if (hasGonghao && hasJiaoshixingming) {
                System.out.println("\n✅ 表结构正确！如果仍无法新增，问题可能在其他地方。");
            } else {
                System.out.println("\n❌ 表结构不完整！请执行以下 SQL:");
                System.out.println("\nALTER TABLE `jingsaixinxi` ");
                if (!hasGonghao) {
                    System.out.println("ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`,");
                }
                if (!hasJiaoshixingming) {
                    System.out.println("ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `" + (hasGonghao ? "gonghao" : "fengmiantupian") + "`;");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ 数据库连接失败：" + e.getMessage());
            System.err.println("\n可能原因：");
            System.err.println("  1. MySQL 服务未启动");
            System.err.println("  2. 数据库密码错误（当前配置：root/123123）");
            System.err.println("  3. 数据库 BYSJ_SpringBoot 不存在");
        }
    }
}
