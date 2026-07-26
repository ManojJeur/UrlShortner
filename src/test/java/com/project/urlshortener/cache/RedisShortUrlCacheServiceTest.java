package com.project.urlshortener.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisShortUrlCacheServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    void putAndGetShouldRoundTripCacheEntry() throws Exception {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        ObjectMapper objectMapper = new ObjectMapper();

        when(template.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("short_url:cache:springtips")).thenReturn("""
                {"originalUrl":"https://example.com","enabled":true,"expiresAt":"2026-08-01T00:00:00Z"}
                """);

        RedisShortUrlCacheService service = new RedisShortUrlCacheService(template, objectMapper);
        ShortUrlCacheEntry entry = new ShortUrlCacheEntry("https://example.com", true, Instant.parse("2026-08-01T00:00:00Z"));

        service.put("springtips", entry, Duration.ofMinutes(5));
        Optional<ShortUrlCacheEntry> cached = service.get("springtips");

        assertTrue(cached.isPresent());
        assertEquals("https://example.com", cached.get().getOriginalUrl());
        verify(valueOperations).set(eq("short_url:cache:springtips"), any(String.class), eq(Duration.ofMinutes(5)));
    }
}
