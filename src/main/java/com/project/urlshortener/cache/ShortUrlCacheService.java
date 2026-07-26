package com.project.urlshortener.cache;

import java.time.Duration;
import java.util.Optional;

public interface ShortUrlCacheService {

    Optional<ShortUrlCacheEntry> get(String shortCode);

    void put(String shortCode, ShortUrlCacheEntry entry, Duration ttl);

    void evict(String shortCode);
}
