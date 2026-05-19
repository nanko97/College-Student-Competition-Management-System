package com.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.OperationLogDao;
import com.entity.OperationLogEntity;
import com.service.OperationLogService;
import com.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 操作日志 Service 实现类
 */
@Slf4j
@Service("operationLogService")
public class OperationLogServiceImpl extends ServiceImpl<OperationLogDao, OperationLogEntity> implements OperationLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 计算分页参数
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int limit = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
        
        Map<String, Object> queryParams = new java.util.HashMap<>();
        queryParams.put("offset", (page - 1) * limit);
        queryParams.put("limit", limit);
        queryParams.put("operator", params.get("operator"));
        queryParams.put("operationModule", params.get("operationModule"));
        queryParams.put("operationType", params.get("operationType"));
        queryParams.put("status", params.get("status"));
        queryParams.put("startTime", params.get("startTime"));
        queryParams.put("endTime", params.get("endTime"));
        
        int total = baseMapper.selectPageCount(queryParams);
        List<OperationLogEntity> records = baseMapper.selectPageList(queryParams);
        
        // 使用正确的PageUtils构造方式
        return new PageUtils(records, total, limit, page);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(OperationLogEntity logEntity) {
        try {
            this.insert(logEntity);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
            // 日志保存失败不影响主业务流程，只记录错误
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOldLogs(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date beforeDate = calendar.getTime();
        
        int deletedCount = baseMapper.deleteLogsBeforeDate(beforeDate);
        log.info("清理{}天前的操作日志，共删除{}条记录", days, deletedCount);
        return deletedCount;
    }
}
