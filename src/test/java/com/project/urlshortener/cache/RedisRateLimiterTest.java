package com.project.urlshortener.cache;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisRateLimiterTest {

    @SuppressWarnings("unchecked")
    @Test
    void tryAcquireShouldAllowRequestsWithinLimit() {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        RedisRateLimiter limiter = new RedisRateLimiter(template);
        boolean allowed = limiter.tryAcquire("rate_limit:auth", "127.0.0.1", 10, Duration.ofSeconds(60));

        assertTrue(allowed);
    }
}
