package com.liviz.v2.controller.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedis() {
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
