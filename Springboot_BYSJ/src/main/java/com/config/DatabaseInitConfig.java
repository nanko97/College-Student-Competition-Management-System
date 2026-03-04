package com.config;

import com.utils.DatabaseInitUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化配置
 * 应用启动时自动执行数据库初始化
 */
@Component
public class DatabaseInitConfig implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================");
        System.out.println("开始数据库初始化检查...");
        System.out.println("=================================");
        
        try {
            DatabaseInitUtil.initDatabase();
        } catch (Exception e) {
            System.err.println("数据库初始化失败：" + e.getMessage());
            System.err.println("请检查 MySQL 是否启动以及数据库配置是否正确");
        }
        
        System.out.println("=================================");
    }
}
