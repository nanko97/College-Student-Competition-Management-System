package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.entity.OperationLogEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 操作日志 DAO
 */
public interface OperationLogDao extends BaseMapper<OperationLogEntity> {
    
    /**
     * 分页查询操作日志
     */
    List<OperationLogEntity> selectPageList(@Param("params") Map<String, Object> params);
    
    /**
     * 统计总数
     */
    int selectPageCount(@Param("params") Map<String, Object> params);
    
    /**
     * 删除指定日期之前的日志
     * @param beforeDate 删除此日期之前的日志
     * @return 删除的记录数
     */
    int deleteLogsBeforeDate(@Param("beforeDate") Date beforeDate);
}
