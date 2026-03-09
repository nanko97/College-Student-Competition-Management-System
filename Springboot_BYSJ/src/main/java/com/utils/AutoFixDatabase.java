package com.utils;

import java.sql.*;

/**
 * 自动修复 jingsaixinxi 表结构
 */
public class AutoFixDatabase {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/springbootrd362?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123123";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("开始检查和修复 jingsaixinxi 表...");
        System.out.println("========================================\n");

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("✓ 数据库连接成功\n");
            
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "jingsaixinxi", null);
            
            boolean hasGonghao = false;
            boolean hasJiaoshixingming = false;
            
            System.out.println("当前字段列表：");
            int count = 0;
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                System.out.println("  " + (++count) + ". " + colName);
                
                if ("gonghao".equals(colName)) hasGonghao = true;
                if ("jiaoshixingming".equals(colName)) hasJiaoshixingming = true;
            }
            
            System.out.println("\n关键字段检查：");
            System.out.println("  gonghao: " + (hasGonghao ? "✓ 存在" : "✗ 缺失"));
            System.out.println("  jiaoshixingming: " + (hasJiaoshixingming ? "✓ 存在" : "✗ 缺失"));
            System.out.println();
            
            if (hasGonghao && hasJiaoshixingming) {
                System.out.println("✅ 表结构完整，无需修复！");
                System.out.println("\n可以直接启动后端服务进行测试。");
                return;
            }
            
            // 需要修复
            System.out.println("⚠️  检测到字段缺失，开始执行修复...\n");
            
            try (Statement stmt = conn.createStatement()) {
                if (!hasGonghao) {
                    System.out.println("正在添加 gonghao 字段...");
                    String sql1 = "ALTER TABLE `jingsaixinxi` ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`";
                    stmt.executeUpdate(sql1);
                    System.out.println("✓ gonghao 字段添加成功\n");
                }
                
                if (!hasJiaoshixingming) {
                    System.out.println("正在添加 jiaoshixingming 字段...");
                    String afterColumn = hasGonghao ? "gonghao" : "fengmiantupian";
                    String sql2 = "ALTER TABLE `jingsaixinxi` ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `" + afterColumn + "`";
                    stmt.executeUpdate(sql2);
                    System.out.println("✓ jiaoshixingming 字段添加成功\n");
                }
                
                // 验证修复结果
                System.out.println("验证修复结果...");
                rs = meta.getColumns(null, null, "jingsaixinxi", null);
                System.out.println("\n修复后的字段列表：");
                count = 0;
                while (rs.next()) {
                    String colName = rs.getString("COLUMN_NAME");
                    System.out.println("  " + (++count) + ". " + colName);
                    
                    if ("gonghao".equals(colName)) hasGonghao = true;
                    if ("jiaoshixingming".equals(colName)) hasJiaoshixingming = true;
                }
                
                System.out.println("\n最终检查：");
                System.out.println("  gonghao: " + (hasGonghao ? "✓ 存在" : "✗ 缺失"));
                System.out.println("  jiaoshixingming: " + (hasJiaoshixingming ? "✓ 存在" : "✗ 缺失"));
                
                if (hasGonghao && hasJiaoshixingming) {
                    System.out.println("\n✅ 数据库修复成功！");
                    System.out.println("\n下一步操作：");
                    System.out.println("1. 重启后端服务：mvn spring-boot:run");
                    System.out.println("2. 清除浏览器缓存：Ctrl+Shift+Delete");
                    System.out.println("3. 测试新增竞赛功能");
                } else {
                    System.out.println("\n❌ 修复失败，请手动执行 SQL");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ 数据库错误：" + e.getMessage());
            System.err.println("\n可能原因：");
            System.err.println("  1. MySQL 服务未启动");
            System.err.println("  2. 数据库配置错误（用户名/密码）");
            System.err.println("  3. 数据库 springbootrd362 不存在");
            System.err.println("\n详细错误信息：");
            e.printStackTrace();
        }
    }
}
