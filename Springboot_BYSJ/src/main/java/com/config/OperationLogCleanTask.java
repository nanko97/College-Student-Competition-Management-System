package com.config;

import com.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 【论文4.4.4】操作日志定时清理任务
 * 日志保存180天，每天凌晨2点自动清理过期日志
 */
@Component
@Slf4j
public class OperationLogCleanTask {

    /** 日志保留天数（论文4.4.4：保存180天） */
    private static final int LOG_RETENTION_DAYS = 180;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 定时清理过期操作日志
     * 每天凌晨2点执行（论文4.4.4要求日志保存180天后自动清理）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredLogs() {
        try {
            log.info("【日志自动清理】开始执行，保留最近{}天的日志...", LOG_RETENTION_DAYS);

            int deletedCount = operationLogService.deleteOldLogs(LOG_RETENTION_DAYS);

            if (deletedCount > 0) {
                log.info("【日志自动清理】清理完成，共删除{}条过期日志记录", deletedCount);
            } else {
                log.info("【日志自动清理】无需清理，所有日志均在保留期内");
            }
        } catch (Exception e) {
            log.error("【日志自动清理】任务执行失败：{}", e.getMessage(), e);
        }
    }
}