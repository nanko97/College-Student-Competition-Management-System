package com.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 实体工具类
 * 功能：提供实体对象的通用操作方法
 * 
 * @author 毕业设计优化版
 * @date 2026-05-21
 */
@Slf4j
public class EntityUtil {
    
    /**
     * 为实体对象设置addtime字段（如果为空）
     * 功能：统一处理实体对象的创建时间，避免前端日期格式问题
     * 
     * @param entity 实体对象
     * @return 处理后的实体对象
     */
    public static <T> T setAddtimeIfNull(T entity) {
        if (entity == null) {
            return entity;
        }
        
        try {
            // 使用反射调用setAddtime方法
            Method method = entity.getClass().getMethod("setAddtime", Date.class);
            Date currentTime = (Date) method.invoke(entity);
            
            // 如果当前addtime为null，则设置为当前时间
            if (currentTime == null) {
                method.invoke(entity, new Date());
                log.debug("为实体 {} 设置addtime为当前时间", entity.getClass().getSimpleName());
            }
        } catch (NoSuchMethodException e) {
            // 实体类没有addtime字段，忽略
            log.debug("实体 {} 没有addtime字段", entity.getClass().getSimpleName());
        } catch (Exception e) {
            log.warn("设置实体addtime失败：{}", e.getMessage());
        }
        
        return entity;
    }
}
