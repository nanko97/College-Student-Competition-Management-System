package com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务类
 */
@Service
@Slf4j
public class CacheService {
    
    // ==================== 常量定义 ====================
    
    /** 竞赛信息缓存前缀 */
    private static final String JINGSAI_CACHE_PREFIX = "jingsai:";
    
    /** 用户信息缓存前缀 */
    private static final String USER_CACHE_PREFIX = "user:";
    
    /** 学生信息缓存前缀 */
    private static final String XUESHENG_CACHE_PREFIX = "xuesheng:";
    
    /** 教师信息缓存前缀 */
    private static final String JIAOSHI_CACHE_PREFIX = "jiaoshi:";
    
    /** 报名信息缓存前缀 */
    private static final String BAOMING_CACHE_PREFIX = "baoming:";
    
    /** 默认缓存时间：30 分钟 */
    private static final long DEFAULT_CACHE_TTL_MINUTES = 30;
    
    /** 用户信息缓存时间：1 小时 */
    private static final long USER_CACHE_TTL_MINUTES = 60;
    
    // ==================== 依赖注入 ====================
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private CacheMonitorService cacheMonitorService;
    
    // ==================== 竞赛信息缓存 ====================
    
    /**
     * 缓存竞赛信息
     * 
     * @param id 竞赛 ID
     * @param entity 竞赛信息实体
     */
    public void cacheJingsai(Long id, Object entity) {
        if (id == null || entity == null) {
            return;
        }
        String key = JINGSAI_CACHE_PREFIX + id;
        redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("缓存竞赛信息：key={}, id={}", key, id);
    }
    
    /**
     * 获取缓存的竞赛信息
     * 
     * @param id 竞赛 ID
     * @return 竞赛信息实体，缓存不存在时返回 null
     */
    public Object getCachedJingsai(Long id) {
        if (id == null) {
            return null;
        }
        String key = JINGSAI_CACHE_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("命中竞赛缓存：key={}, id={}", key, id);
        }
        return cached;
    }
    
    /**
     * 删除竞赛缓存
     * 
     * @param id 竞赛 ID
     */
    public void deleteJingsaiCache(Long id) {
        if (id == null) {
            return;
        }
        String key = JINGSAI_CACHE_PREFIX + id;
        redisTemplate.delete(key);
        log.debug("删除竞赛缓存：key={}", key);
    }
    
    // ==================== 用户信息缓存 ====================
    
    /**
     * 缓存用户信息
     * 
     * @param username 用户名
     * @param user 用户实体
     */
    public void cacheUser(String username, Object user) {
        if (username == null || user == null) {
            return;
        }
        String key = USER_CACHE_PREFIX + username;
        redisTemplate.opsForValue().set(key, user, USER_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("缓存用户信息：key={}, username={}", key, username);
    }
    
    /**
     * 获取缓存的用户信息
     * 
     * @param username 用户名
     * @return 用户实体，缓存不存在时返回 null
     */
    public Object getCachedUser(String username) {
        if (username == null) {
            return null;
        }
        String key = USER_CACHE_PREFIX + username;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("命中用户缓存：key={}, username={}", key, username);
        }
        return cached;
    }
    
    /**
     * 删除用户缓存
     * 
     * @param username 用户名
     */
    public void deleteUserCache(String username) {
        if (username == null) {
            return;
        }
        String key = USER_CACHE_PREFIX + username;
        redisTemplate.delete(key);
        log.debug("删除用户缓存：key={}", key);
    }
    
    // ==================== 学生信息缓存 ====================
    
    /**
     * 缓存学生信息
     * 
     * @param xuehao 学号
     * @param entity 学生实体
     */
    public void cacheXuesheng(String xuehao, Object entity) {
        if (xuehao == null || entity == null) {
            return;
        }
        String key = XUESHENG_CACHE_PREFIX + xuehao;
        redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("缓存学生信息：key={}, xuehao={}", key, xuehao);
    }
    
    /**
     * 获取缓存的学生信息
     * 
     * @param xuehao 学号
     * @return 学生实体，缓存不存在时返回 null
     */
    public Object getCachedXuesheng(String xuehao) {
        if (xuehao == null) {
            return null;
        }
        String key = XUESHENG_CACHE_PREFIX + xuehao;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("命中学生缓存：key={}, xuehao={}", key, xuehao);
        }
        return cached;
    }
    
    /**
     * 删除学生缓存
     * 
     * @param xuehao 学号
     */
    public void deleteXueshengCache(String xuehao) {
        if (xuehao == null) {
            return;
        }
        String key = XUESHENG_CACHE_PREFIX + xuehao;
        redisTemplate.delete(key);
        log.debug("删除学生缓存：key={}", key);
    }
    
    // ==================== 教师信息缓存 ====================
    
    /**
     * 缓存教师信息
     * 
     * @param gonghao 工号
     * @param entity 教师实体
     */
    public void cacheJiaoshi(String gonghao, Object entity) {
        if (gonghao == null || entity == null) {
            return;
        }
        String key = JIAOSHI_CACHE_PREFIX + gonghao;
        redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("缓存教师信息：key={}, gonghao={}", key, gonghao);
    }
    
    /**
     * 获取缓存的教师信息
     * 
     * @param gonghao 工号
     * @return 教师实体，缓存不存在时返回 null
     */
    public Object getCachedJiaoshi(String gonghao) {
        if (gonghao == null) {
            return null;
        }
        String key = JIAOSHI_CACHE_PREFIX + gonghao;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("命中教师缓存：key={}, gonghao={}", key, gonghao);
        }
        return cached;
    }
    
    /**
     * 删除教师缓存
     * 
     * @param gonghao 工号
     */
    public void deleteJiaoshiCache(String gonghao) {
        if (gonghao == null) {
            return;
        }
        String key = JIAOSHI_CACHE_PREFIX + gonghao;
        redisTemplate.delete(key);
        log.debug("删除教师缓存：key={}", key);
    }
    
    // ==================== 报名信息缓存 ====================
    
    /**
     * 缓存报名信息
     * 
     * @param id 报名 ID
     * @param entity 报名实体
     */
    public void cacheBaoming(Long id, Object entity) {
        if (id == null || entity == null) {
            return;
        }
        String key = BAOMING_CACHE_PREFIX + id;
        redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("缓存报名信息：key={}, id={}", key, id);
    }
    
    /**
     * 获取缓存的报名信息
     * 
     * @param id 报名 ID
     * @return 报名实体，缓存不存在时返回 null
     */
    public Object getCachedBaoming(Long id) {
        if (id == null) {
            return null;
        }
        String key = BAOMING_CACHE_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("命中报名缓存：key={}, id={}", key, id);
        }
        return cached;
    }
    
    /**
     * 删除报名缓存
     * 
     * @param id 报名 ID
     */
    public void deleteBaomingCache(Long id) {
        if (id == null) {
            return;
        }
        String key = BAOMING_CACHE_PREFIX + id;
        redisTemplate.delete(key);
        log.debug("删除报名缓存：key={}", key);
    }
    
    // ==================== 通用缓存操作 ====================
    
    /**
     * 缓存任意对象
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param ttlMinutes 过期时间（分钟）
     */
    public void cacheObject(String key, Object value, long ttlMinutes) {
        if (key == null || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
        log.debug("缓存对象：key={}", key);
    }
    
    /**
     * 获取缓存的对象
     * 
     * @param key 缓存键
     * @return 缓存值，不存在时返回 null
     */
    public Object getCachedObject(String key) {
        if (key == null) {
            return null;
        }
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 删除缓存
     * 
     * @param key 缓存键
     */
    public void deleteCache(String key) {
        if (key == null) {
            return;
        }
        redisTemplate.delete(key);
        log.debug("删除缓存：key={}", key);
    }
    
    /**
     * 检查缓存是否存在
     * 
     * @param key 缓存键
     * @return true-存在，false-不存在
     */
    public boolean hasCache(String key) {
        if (key == null) {
            return false;
        }
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
    
    /**
     * 设置缓存过期时间
     * 
     * @param key 缓存键
     * @param ttlMinutes 过期时间（分钟）
     * @return true-设置成功，false-设置失败
     */
    public boolean setCacheExpire(String key, long ttlMinutes) {
        if (key == null) {
            return false;
        }
        return redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
    }
}
