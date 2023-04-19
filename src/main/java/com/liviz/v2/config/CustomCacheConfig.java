package com.liviz.v2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

@Configuration
@EnableCaching
public class CustomCacheConfig {

    @Value("${spring.redis.host:}")
    private String redisHost;

    @Bean
    public CacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        if (redisHost == null || redisHost.isEmpty()) {
            return new org.springframework.cache.support.NoOpCacheManager();
        } else {
            return RedisCacheManager.builder(Objects.requireNonNull(redisTemplate.getConnectionFactory())).build();
        }
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
