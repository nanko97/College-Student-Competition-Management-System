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
        try {
            DatabaseInitUtil.initDatabase();
        } catch (Exception e) {
            // 静默失败，不影响启动
        }
    }
}
