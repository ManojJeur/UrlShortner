package com.project.urlshortener.service.impl;

import com.project.urlshortener.constants.ErrorCodes;
import com.project.urlshortener.config.RedisAppProperties;
import com.project.urlshortener.config.ShortUrlProperties;
import com.project.urlshortener.cache.ShortUrlCacheEntry;
import com.project.urlshortener.cache.ShortUrlCacheService;
import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.dto.response.ShortUrlResponse;
import com.project.urlshortener.entity.ShortUrl;
import com.project.urlshortener.entity.User;
import com.project.urlshortener.exception.BadRequestException;
import com.project.urlshortener.exception.ConflictException;
import com.project.urlshortener.exception.ResourceNotFoundException;
import com.project.urlshortener.mapper.ShortUrlMapper;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.repository.UserRepository;
import com.project.urlshortener.service.UrlService;
import com.project.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Service
public class UrlServiceImpl implements UrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ShortUrlProperties shortUrlProperties;
    private final RedisAppProperties redisAppProperties;
    private final ShortUrlCacheService shortUrlCacheService;

    public UrlServiceImpl(ShortUrlRepository shortUrlRepository,
                          UserRepository userRepository,
                          ShortCodeGenerator shortCodeGenerator,
                          ShortUrlProperties shortUrlProperties,
                          RedisAppProperties redisAppProperties,
                          ShortUrlCacheService shortUrlCacheService) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.shortUrlProperties = shortUrlProperties;
        this.redisAppProperties = redisAppProperties;
        this.shortUrlCacheService = shortUrlCacheService;
    }

    @Override
    @Transactional
    public ShortUrlResponse createShortUrl(String createdByEmail, CreateShortUrlRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(createdByEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCodes.RESOURCE_NOT_FOUND));

        String originalUrl = normalizeAndValidateUrl(request.getOriginalUrl());
        validateExpiry(request.getExpiresAt());
        Instant now = Instant.now();
        String requestedAlias = normalizeAlias(request.getCustomAlias());
        String shortCode = requestedAlias != null ? requestedAlias : generateUniqueShortCode();

        if (shortUrlRepository.existsByShortCode(shortCode)) {
            throw new ConflictException("Short code already exists", ErrorCodes.CONFLICT);
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setShortCode(shortCode);
        shortUrl.setCustomAlias(requestedAlias);
        shortUrl.setClickCount(0L);
        shortUrl.setEnabled(true);
        shortUrl.setExpiresAt(request.getExpiresAt());
        shortUrl.setCreatedBy(user);
        shortUrl.setCreatedAt(now);
        shortUrl.setUpdatedAt(now);

        ShortUrl savedShortUrl = shortUrlRepository.save(shortUrl);
        cacheShortUrl(savedShortUrl);
        return ShortUrlMapper.toResponse(savedShortUrl, shortUrlProperties);
    }

    @Override
    @Transactional
    public String resolveOriginalUrl(String shortCode) {
        String normalizedShortCode = normalizeAlias(shortCode);
        if (normalizedShortCode == null) {
            throw new BadRequestException("Short code is required", ErrorCodes.BAD_REQUEST);
        }
        ShortUrlCacheEntry cacheEntry = shortUrlCacheService.get(normalizedShortCode).orElse(null);
        if (cacheEntry != null) {
            validateCacheEntry(cacheEntry);
            incrementClickCount(normalizedShortCode);
            return cacheEntry.getOriginalUrl();
        }

        ShortUrl shortUrl = shortUrlRepository.findByShortCode(normalizedShortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found", ErrorCodes.RESOURCE_NOT_FOUND));
        validateShortUrl(shortUrl);
        cacheShortUrl(shortUrl);
        incrementClickCount(normalizedShortCode);
        return shortUrl.getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < shortUrlProperties.getGenerationAttempts(); attempt++) {
            String candidate = shortCodeGenerator.generate(shortUrlProperties.getCodeLength());
            if (!shortUrlRepository.existsByShortCode(candidate)) {
                return candidate;
            }
        }
        throw new ConflictException("Unable to generate a unique short code", ErrorCodes.CONFLICT);
    }

    private String normalizeAndValidateUrl(String originalUrl) {
        String trimmed = originalUrl == null ? null : originalUrl.trim();
        if (trimmed == null || trimmed.isBlank()) {
            throw new BadRequestException("Original URL is required", ErrorCodes.BAD_REQUEST);
        }

        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new BadRequestException("Original URL must start with http or https", ErrorCodes.BAD_REQUEST);
            }
            if (uri.getHost() == null) {
                throw new BadRequestException("Original URL must be a valid absolute URL", ErrorCodes.BAD_REQUEST);
            }
            return uri.toString();
        } catch (URISyntaxException exception) {
            throw new BadRequestException("Original URL is invalid", ErrorCodes.BAD_REQUEST);
        }
    }

    private String normalizeAlias(String alias) {
        if (alias == null) {
            return null;
        }
        String normalized = alias.trim().toLowerCase(Locale.ROOT);
        return normalized.isBlank() ? null : normalized;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private void cacheShortUrl(ShortUrl shortUrl) {
        if (shortUrl == null || shortUrl.getShortCode() == null) {
            return;
        }
        Duration ttl = resolveCacheTtl(shortUrl.getExpiresAt());
        shortUrlCacheService.put(
                shortUrl.getShortCode(),
                new ShortUrlCacheEntry(shortUrl.getOriginalUrl(), shortUrl.isEnabled(), shortUrl.getExpiresAt()),
                ttl
        );
    }

    private Duration resolveCacheTtl(Instant expiresAt) {
        if (expiresAt == null) {
            return Duration.ofSeconds(redisAppProperties.getUrlCacheTtlSeconds());
        }
        long seconds = Duration.between(Instant.now(), expiresAt).getSeconds();
        if (seconds <= 0) {
            return Duration.ofSeconds(1);
        }
        return Duration.ofSeconds(seconds);
    }

    private void validateExpiry(Instant expiresAt) {
        if (expiresAt != null && !expiresAt.isAfter(Instant.now())) {
            throw new BadRequestException("Expiry date must be in the future", ErrorCodes.BAD_REQUEST);
        }
    }

    private void validateCacheEntry(ShortUrlCacheEntry cacheEntry) {
        if (!cacheEntry.isEnabled()) {
            throw new ResourceNotFoundException("Short URL is disabled", ErrorCodes.RESOURCE_NOT_FOUND);
        }
        if (cacheEntry.getExpiresAt() != null && cacheEntry.getExpiresAt().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("Short URL has expired", ErrorCodes.RESOURCE_NOT_FOUND);
        }
    }

    private void validateShortUrl(ShortUrl shortUrl) {
        if (!shortUrl.isEnabled()) {
            throw new ResourceNotFoundException("Short URL is disabled", ErrorCodes.RESOURCE_NOT_FOUND);
        }
        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("Short URL has expired", ErrorCodes.RESOURCE_NOT_FOUND);
        }
    }

    private void incrementClickCount(String shortCode) {
        shortUrlRepository.incrementClickCountByShortCode(shortCode, Instant.now());
    }
}
