package com.project.urlshortener.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisRateLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean tryAcquire(String keyPrefix, String identifier, int maxRequests, Duration window) {
        String redisKey = keyPrefix + ":" + identifier + ":" + (System.currentTimeMillis() / window.toMillis());
        Long currentCount = stringRedisTemplate.opsForValue().increment(redisKey);
        if (currentCount == null) {
            return false;
        }
        if (currentCount == 1L) {
            stringRedisTemplate.expire(redisKey, window);
        }
        return currentCount <= maxRequests;
    }
}
