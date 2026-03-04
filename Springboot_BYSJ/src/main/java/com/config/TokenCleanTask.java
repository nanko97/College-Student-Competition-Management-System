package com.config;

import com.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Token 定时清理任务
 * 每 10 分钟清理一次过期的 token 记录
 */
@Component
@Slf4j
public class TokenCleanTask {
    
    @Autowired
    private TokenService tokenService;
    
    /**
     * 定时清理过期 Token
     * 每 10 分钟执行一次（600000 毫秒）
     */
    @Scheduled(fixedRate = 600000)
    public void cleanExpiredTokens() {
        try {
            log.info("开始执行 Token 过期清理任务...");
            
            int cleanedCount = tokenService.cleanExpiredTokens();
            
            log.info("Token 过期清理完成，共清理 {} 条过期记录", cleanedCount);
        } catch (Exception e) {
            log.error("Token 过期清理任务执行失败：{}", e.getMessage(), e);
        }
    }
}
