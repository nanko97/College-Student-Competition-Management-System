package com;

import com.utils.AutoFixDatabase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时自动检查并修复数据库表结构
 */
@Component
public class DatabaseFixRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("开始检查数据库表结构...");
        System.out.println("========================================\n");
        
        // 运行数据库修复工具
        AutoFixDatabase.main(args);
        
        System.out.println("\n========================================");
        System.out.println("数据库检查完成！");
        System.out.println("========================================\n");
    }
}
