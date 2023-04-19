package com.liviz.v2.controller.redis;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisTest {
    @Autowired
    CacheManager cacheManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedis() {
        // Skip the test if Redis is not configured
        Assumptions.assumeTrue(cacheManager instanceof RedisCacheManager, "Redis is not configured, skipping test");

        // Set a key-value pair in Redis
        String key = "myKey";
        String value = "myValue";
        redisTemplate.opsForValue().set(key, value);

        // Get the value from Redis using the same key
        String retrievedValue = redisTemplate.opsForValue().get(key);

        // Assert that the retrieved value is the same as the one that was set
        assertEquals(value, retrievedValue, "The value retrieved from Redis should match the one that was set");
    }
}
