package com.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类 - 高性能封装
 */
@Component
public class CacheUtil {

    @Autowired
    private static RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        CacheUtil.redisTemplate = redisTemplate;
    }

    /**
     * 设置缓存 (永不过期)
     */
    public static void set(String key, Object value) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存 (指定过期时间)
     * @param timeout 超时时间 (秒)
     */
    public static void set(String key, Object value, long timeout) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     */
    public static Object get(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public static boolean delete(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 判断 key 是否存在
     */
    public static boolean hasKey(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间 (秒)
     */
    public static boolean expire(String key, long timeout) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
    }

    /**
     * 获取过期时间 (秒)
     */
    public static Long getExpire(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 自增 (适用于计数器场景)
     */
    public static Long increment(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自减 (适用于计数器场景)
     */
    public static Long decrement(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate not initialized");
        }
        return redisTemplate.opsForValue().decrement(key);
    }
}
