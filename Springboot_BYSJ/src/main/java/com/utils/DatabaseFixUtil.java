package com.utils;

import java.sql.*;

/**
 * 数据库修复工具类
 * 用于执行 jingsaixinxi 表的字段迁移
 * 
 * 使用方法：
 * 1. 修改 main 方法中的数据库连接信息（如果需要）
 * 2. 运行此类的 main 方法
 * 3. 查看控制台输出，确认是否成功
 */
public class DatabaseFixUtil {

    // 数据库配置（从 application.yml 复制）
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/springbootrd362?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123123";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("开始修复 jingsaixinxi 表结构...");
        System.out.println("========================================");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 1. 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL 驱动加载成功");

            // 2. 建立连接
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✓ 数据库连接成功：" + URL);

            // 3. 检查字段是否存在
            boolean hasGonghao = false;
            boolean hasJiaoshixingming = false;

            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getColumns(null, null, "jingsaixinxi", null);

            System.out.println("\n当前 jingsaixinxi 表字段：");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                System.out.println("  - " + columnName);
                if ("gonghao".equals(columnName)) {
                    hasGonghao = true;
                }
                if ("jiaoshixingming".equals(columnName)) {
                    hasJiaoshixingming = true;
                }
            }

            // 4. 执行迁移
            stmt = conn.createStatement();

            if (!hasGonghao) {
                System.out.println("\n添加 gonghao 字段...");
                String sql1 = "ALTER TABLE `jingsaixinxi` ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`";
                stmt.executeUpdate(sql1);
                System.out.println("✓ gonghao 字段添加成功");
            } else {
                System.out.println("\n✓ gonghao 字段已存在，跳过");
            }

            if (!hasJiaoshixingming) {
                System.out.println("添加 jiaoshixingming 字段...");
                String sql2 = "ALTER TABLE `jingsaixinxi` ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`";
                stmt.executeUpdate(sql2);
                System.out.println("✓ jiaoshixingming 字段添加成功");
            } else {
                System.out.println("✓ jiaoshixingming 字段已存在，跳过");
            }

            // 5. 验证结果
            System.out.println("\n验证表结构...");
            rs = stmt.executeQuery("DESCRIBE jingsaixinxi");
            System.out.println("\n修复后的 jingsaixinxi 表结构：");
            while (rs.next()) {
                String colName = rs.getString("Field");
                String colType = rs.getString("Type");
                String nullable = rs.getString("Null");
                System.out.println("  " + colName + " | " + colType + " | " + nullable);
            }

            System.out.println("\n========================================");
            System.out.println("✅ 数据库修复完成！");
            System.out.println("========================================");
            System.out.println("\n下一步操作：");
            System.out.println("1. 重启后端服务：mvn spring-boot:run");
            System.out.println("2. 重新编译前端（可选）：npm run serve");
            System.out.println("3. 测试新增竞赛功能");
            System.out.println("\n详细说明请查看：FIX_DATABASE.md");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ 错误：找不到 MySQL 驱动");
            System.err.println("请确保项目中包含 mysql-connector-java 依赖");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ 数据库错误：" + e.getMessage());
            System.err.println("\n可能的原因：");
            System.err.println("1. MySQL 服务未启动");
            System.err.println("2. 数据库名、用户名或密码错误");
            System.err.println("3. 数据库 springbootrd362 不存在");
            System.err.println("\n请检查 application.yml 中的数据库配置");
            e.printStackTrace();
        } finally {
            // 6. 关闭资源
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
