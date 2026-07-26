package com.project.urlshortener.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisShortUrlCacheService implements ShortUrlCacheService {

    private static final String CACHE_PREFIX = "short_url:cache:";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisShortUrlCacheService(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<ShortUrlCacheEntry> get(String shortCode) {
        String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey(shortCode));
        if (cachedValue == null || cachedValue.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(cachedValue, ShortUrlCacheEntry.class));
        } catch (Exception exception) {
            stringRedisTemplate.delete(cacheKey(shortCode));
            return Optional.empty();
        }
    }

    @Override
    public void put(String shortCode, ShortUrlCacheEntry entry, Duration ttl) {
        try {
            stringRedisTemplate.opsForValue().set(cacheKey(shortCode), objectMapper.writeValueAsString(entry), ttl);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize short URL cache entry", exception);
        }
    }

    @Override
    public void evict(String shortCode) {
        stringRedisTemplate.delete(cacheKey(shortCode));
    }

    private String cacheKey(String shortCode) {
        return CACHE_PREFIX + shortCode;
    }
}
