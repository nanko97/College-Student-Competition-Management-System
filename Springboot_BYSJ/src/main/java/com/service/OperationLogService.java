package com.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.entity.OperationLogEntity;
import com.utils.PageUtils;

import java.util.Date;
import java.util.Map;

/**
 * 操作日志 Service 接口
 */
public interface OperationLogService extends IService<OperationLogEntity> {

    /**
     * 分页查询操作日志
     */
    PageUtils queryPage(Map<String, Object> params);
    
    /**
     * 保存操作日志
     */
    void saveLog(OperationLogEntity logEntity);
    
    /**
     * 删除指定日期之前的日志（用于定期清理）
     * @param days 保留天数，删除此天数之前的日志
     * @return 删除的记录数
     */
    int deleteOldLogs(int days);
}
