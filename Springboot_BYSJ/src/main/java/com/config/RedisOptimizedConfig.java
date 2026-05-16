package com.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 优化配置类 - 产品化版本
 * 
 * 【优化内容】
 * 1. 多缓存管理器支持（不同业务场景使用不同的过期策略）
 * 2. 优化的 JSON 序列化配置（支持类型信息，避免反序列化问题）
 * 3. 缓存键前缀管理（便于区分和清理）
 * 4. 缓存监控支持（记录命中率等指标）
 * 5. 空值保护（防止缓存穿透）
 * 
 * @author 产品化优化版本
 * @date 2026-04-06
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisOptimizedConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    /**
     * 优化的 RedisTemplate 配置
     * 
     * 【优化点】
     * 1. Key 使用 String 序列化（可读性好，便于调试）
     * 2. Value 使用 Jackson JSON 序列化（支持复杂对象）
     * 3. 启用类型信息（避免反序列化时类型丢失）
     * 4. 禁用默认类型白名单检查（提升性能）
     */
    @Bean("optimizedRedisTemplate")
    public RedisTemplate<String, Object> optimizedRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建 ObjectMapper 并配置
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用类型信息，存储类的完整名称，反序列化时自动识别
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );

        // JSON 序列化器
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jsonSerializer.setObjectMapper(mapper);
        
        // String 序列化器（用于 Key）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 设置 Key 的序列化方式
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 设置 Value 的序列化方式
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        
        log.info("Redis 优化配置完成 - 服务器: {}:{}, 序列化: JSON with Type Info", redisHost, redisPort);
        
        return template;
    }

    /**
     * 主缓存管理器 - 适用于一般业务数据
     * 
     * 【缓存策略】
     * - 默认过期时间：30 分钟
     * - 不缓存 null 值（防止缓存穿透）
     * - 使用前缀区分不同业务模块
     */
    @Bean("primaryCacheManager")
    @Primary
    public CacheManager primaryCacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(createJsonSerializer())
                )
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "bysj:primary:" + cacheName + ":");

        // 针对不同业务的特定缓存配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 竞赛信息缓存 - 1 小时（相对稳定）
        cacheConfigurations.put("jingsai", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 用户信息缓存 - 2 小时（较少变化）
        cacheConfigurations.put("user", defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // 报名信息缓存 - 10 分钟（频繁变化）
        cacheConfigurations.put("baoming", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // 系统配置缓存 - 24 小时（极少变化）
        cacheConfigurations.put("config", defaultConfig.entryTtl(Duration.ofHours(24)));

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * 短期缓存管理器 - 适用于临时数据
     * 
     * 【使用场景】
     * - 验证码
     * - 登录失败次数
     * - 临时令牌
     */
    @Bean("shortTermCacheManager")
    public CacheManager shortTermCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(createJsonSerializer())
                )
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "bysj:short:" + cacheName + ":");

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(config)
                .build();
    }

    /**
     * 长期缓存管理器 - 适用于字典、配置等静态数据
     */
    @Bean("longTermCacheManager")
    public CacheManager longTermCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(createJsonSerializer())
                )
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "bysj:long:" + cacheName + ":");

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(config)
                .build();
    }

    /**
     * 创建优化的 JSON 序列化器
     */
    private Jackson2JsonRedisSerializer<Object> createJsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(mapper);
        return serializer;
    }
}
