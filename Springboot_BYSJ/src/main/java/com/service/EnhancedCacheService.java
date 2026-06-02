package com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 增强缓存服务
 */
@Service
@Slf4j
public class EnhancedCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private CacheMonitorService cacheMonitorService;

    // ==================== 常量定义 ====================
    
    private static final String JINGSAI_CACHE_PREFIX = "jingsai:";
    private static final String USER_CACHE_PREFIX = "user:";
    private static final String XUESHENG_CACHE_PREFIX = "xuesheng:";
    private static final String JIAOSHI_CACHE_PREFIX = "jiaoshi:";
    private static final String BAOMING_CACHE_PREFIX = "baoming:";
    
    private static final long DEFAULT_CACHE_TTL_MINUTES = 30;
    private static final long USER_CACHE_TTL_MINUTES = 60;

    // ==================== 竞赛信息缓存 ====================

    /**
     * 缓存竞赛信息（带监控）
     */
    public void cacheJingsai(Long id, Object entity) {
        if (id == null || entity == null) {
            return;
        }
        try {
            String key = JINGSAI_CACHE_PREFIX + id;
            redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("缓存竞赛信息：key={}, id={}", key, id);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordSet();
            }
        } catch (Exception e) {
            log.error("缓存竞赛信息失败：id={}", id, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    /**
     * 获取缓存的竞赛信息（带监控）
     */
    public Object getCachedJingsai(Long id) {
        if (id == null) {
            return null;
        }
        try {
            String key = JINGSAI_CACHE_PREFIX + id;
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached != null) {
                log.debug("命中竞赛缓存：key={}, id={}", key, id);
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordHit();
                }
            } else {
                log.debug("竞赛缓存未命中：id={}", id);
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordMiss();
                }
            }
            
            return cached;
        } catch (Exception e) {
            log.error("获取竞赛缓存失败：id={}", id, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
            return null;
        }
    }

    /**
     * 删除竞赛缓存（带监控）
     */
    public void deleteJingsaiCache(Long id) {
        if (id == null) {
            return;
        }
        try {
            String key = JINGSAI_CACHE_PREFIX + id;
            redisTemplate.delete(key);
            log.debug("删除竞赛缓存：key={}", key);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordDelete();
            }
        } catch (Exception e) {
            log.error("删除竞赛缓存失败：id={}", id, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    // ==================== 用户信息缓存 ====================

    /**
     * 缓存用户信息（带监控）
     */
    public void cacheUser(String username, Object user) {
        if (username == null || user == null) {
            return;
        }
        try {
            String key = USER_CACHE_PREFIX + username;
            redisTemplate.opsForValue().set(key, user, USER_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("缓存用户信息：key={}, username={}", key, username);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordSet();
            }
        } catch (Exception e) {
            log.error("缓存用户信息失败：username={}", username, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    /**
     * 获取缓存的用户信息（带监控）
     */
    public Object getCachedUser(String username) {
        if (username == null) {
            return null;
        }
        try {
            String key = USER_CACHE_PREFIX + username;
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached != null) {
                log.debug("命中用户缓存：key={}, username={}", key, username);
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordHit();
                }
            } else {
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordMiss();
                }
            }
            
            return cached;
        } catch (Exception e) {
            log.error("获取用户缓存失败：username={}", username, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
            return null;
        }
    }

    /**
     * 删除用户缓存（带监控）
     */
    public void deleteUserCache(String username) {
        if (username == null) {
            return;
        }
        try {
            String key = USER_CACHE_PREFIX + username;
            redisTemplate.delete(key);
            log.debug("删除用户缓存：key={}", key);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordDelete();
            }
        } catch (Exception e) {
            log.error("删除用户缓存失败：username={}", username, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    // ==================== 学生信息缓存 ====================

    /**
     * 缓存学生信息（带监控）
     */
    public void cacheXuesheng(String xuehao, Object entity) {
        if (xuehao == null || entity == null) {
            return;
        }
        try {
            String key = XUESHENG_CACHE_PREFIX + xuehao;
            redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("缓存学生信息：key={}, xuehao={}", key, xuehao);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordSet();
            }
        } catch (Exception e) {
            log.error("缓存学生信息失败：xuehao={}", xuehao, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    /**
     * 获取缓存的学生信息（带监控）
     */
    public Object getCachedXuesheng(String xuehao) {
        if (xuehao == null) {
            return null;
        }
        try {
            String key = XUESHENG_CACHE_PREFIX + xuehao;
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached != null) {
                log.debug("命中学生缓存：key={}, xuehao={}", key, xuehao);
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordHit();
                }
            } else {
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordMiss();
                }
            }
            
            return cached;
        } catch (Exception e) {
            log.error("获取学生缓存失败：xuehao={}", xuehao, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
            return null;
        }
    }

    /**
     * 删除学生缓存（带监控）
     */
    public void deleteXueshengCache(String xuehao) {
        if (xuehao == null) {
            return;
        }
        try {
            String key = XUESHENG_CACHE_PREFIX + xuehao;
            redisTemplate.delete(key);
            log.debug("删除学生缓存：key={}", key);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordDelete();
            }
        } catch (Exception e) {
            log.error("删除学生缓存失败：xuehao={}", xuehao, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    // ==================== 教师信息缓存 ====================

    /**
     * 缓存教师信息（带监控）
     */
    public void cacheJiaoshi(String gonghao, Object entity) {
        if (gonghao == null || entity == null) {
            return;
        }
        try {
            String key = JIAOSHI_CACHE_PREFIX + gonghao;
            redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("缓存教师信息：key={}, gonghao={}", key, gonghao);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordSet();
            }
        } catch (Exception e) {
            log.error("缓存教师信息失败：gonghao={}", gonghao, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    /**
     * 获取缓存的教师信息（带监控）
     */
    public Object getCachedJiaoshi(String gonghao) {
        if (gonghao == null) {
            return null;
        }
        try {
            String key = JIAOSHI_CACHE_PREFIX + gonghao;
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached != null) {
                log.debug("命中教师缓存：key={}, gonghao={}", key, gonghao);
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordHit();
                }
            } else {
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordMiss();
                }
            }
            
            return cached;
        } catch (Exception e) {
            log.error("获取教师缓存失败：gonghao={}", gonghao, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
            return null;
        }
    }

    /**
     * 删除教师缓存（带监控）
     */
    public void deleteJiaoshiCache(String gonghao) {
        if (gonghao == null) {
            return;
        }
        try {
            String key = JIAOSHI_CACHE_PREFIX + gonghao;
            redisTemplate.delete(key);
            log.debug("删除教师缓存：key={}", key);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordDelete();
            }
        } catch (Exception e) {
            log.error("删除教师缓存失败：gonghao={}", gonghao, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    // ==================== 报名信息缓存 ====================

    /**
     * 缓存报名信息（带监控）
     */
    public void cacheBaoming(Long id, Object entity) {
        if (id == null || entity == null) {
            return;
        }
        try {
            String key = BAOMING_CACHE_PREFIX + id;
            redisTemplate.opsForValue().set(key, entity, DEFAULT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("缓存报名信息：key={}, id={}", key, id);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordSet();
            }
        } catch (Exception e) {
            log.error("缓存报名信息失败：id={}", id, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    /**
     * 获取缓存的报名信息（带监控）
     */
    public Object getCachedBaoming(Long id) {
        if (id == null) {
            return null;
        }
        try {
            String key = BAOMING_CACHE_PREFIX + id;
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached != null) {
                log.debug("命中报名缓存：key={}, id={}", key, id);
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordHit();
                }
            } else {
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordMiss();
                }
            }
            
            return cached;
        } catch (Exception e) {
            log.error("获取报名缓存失败：id={}", id, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
            return null;
        }
    }

    /**
     * 删除报名缓存（带监控）
     */
    public void deleteBaomingCache(Long id) {
        if (id == null) {
            return;
        }
        try {
            String key = BAOMING_CACHE_PREFIX + id;
            redisTemplate.delete(key);
            log.debug("删除报名缓存：key={}", key);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordDelete();
            }
        } catch (Exception e) {
            log.error("删除报名缓存失败：id={}", id, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    // ==================== 通用缓存操作 ====================

    /**
     * 缓存任意对象（带监控）
     */
    public void cacheObject(String key, Object value, long ttlMinutes) {
        if (key == null || value == null) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, value, ttlMinutes, TimeUnit.MINUTES);
            log.debug("缓存对象：key={}", key);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordSet();
            }
        } catch (Exception e) {
            log.error("缓存对象失败：key={}", key, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    /**
     * 获取缓存的对象（带监控）
     */
    public Object getCachedObject(String key) {
        if (key == null) {
            return null;
        }
        try {
            Object value = redisTemplate.opsForValue().get(key);
            
            if (value != null) {
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordHit();
                }
            } else {
                if (cacheMonitorService != null) {
                    cacheMonitorService.recordMiss();
                }
            }
            
            return value;
        } catch (Exception e) {
            log.error("获取缓存对象失败：key={}", key, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
            return null;
        }
    }

    /**
     * 删除缓存（带监控）
     */
    public void deleteCache(String key) {
        if (key == null) {
            return;
        }
        try {
            redisTemplate.delete(key);
            log.debug("删除缓存：key={}", key);
            
            if (cacheMonitorService != null) {
                cacheMonitorService.recordDelete();
            }
        } catch (Exception e) {
            log.error("删除缓存失败：key={}", key, e);
            if (cacheMonitorService != null) {
                cacheMonitorService.recordError();
            }
        }
    }

    /**
     * 检查缓存是否存在
     */
    public boolean hasCache(String key) {
        if (key == null) {
            return false;
        }
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("检查缓存存在性失败：key={}", key, e);
            return false;
        }
    }

    /**
     * 设置缓存过期时间
     */
    public boolean setCacheExpire(String key, long ttlMinutes) {
        if (key == null) {
            return false;
        }
        try {
            return redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("设置缓存过期时间失败：key={}", key, e);
            return false;
        }
    }
}
