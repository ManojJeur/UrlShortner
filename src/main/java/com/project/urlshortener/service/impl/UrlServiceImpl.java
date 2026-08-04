package com.project.urlshortener.service.impl;

import com.project.urlshortener.constants.ErrorCodes;
import com.project.urlshortener.config.RedisAppProperties;
import com.project.urlshortener.config.ShortUrlProperties;
import com.project.urlshortener.cache.ShortUrlCacheEntry;
import com.project.urlshortener.cache.ShortUrlCacheService;
import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.dto.request.ShortUrlSearchRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlStatusRequest;
import com.project.urlshortener.dto.response.PageResponse;
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
import com.project.urlshortener.specification.ShortUrlSpecifications;
import com.project.urlshortener.util.ShortCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UrlServiceImpl implements UrlService {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("createdAt", "updatedAt", "clickCount", "shortCode", "expiresAt", "originalUrl");
    private static final int MAX_PAGE_SIZE = 100;

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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ShortUrlResponse> listMyShortUrls(String createdByEmail, ShortUrlSearchRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(createdByEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCodes.RESOURCE_NOT_FOUND));

        Pageable pageable = toPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortDirection());
        Specification<ShortUrl> specification = ShortUrlSpecifications.ownerView(
                user,
                request.getQuery(),
                request.getEnabled(),
                request.getExpired(),
                request.getHasCustomAlias()
        );

        Page<ShortUrl> page = shortUrlRepository.findAll(specification, pageable);
        List<ShortUrlResponse> content = new ArrayList<>();
        for (ShortUrl shortUrl : page.getContent()) {
            content.add(ShortUrlMapper.toResponse(shortUrl, shortUrlProperties));
        }

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Override
    @Transactional
    public ShortUrlResponse updateShortUrl(String createdByEmail, Long shortUrlId, UpdateShortUrlRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(createdByEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCodes.RESOURCE_NOT_FOUND));

        ShortUrl shortUrl = shortUrlRepository.findByIdAndCreatedByEmail(shortUrlId, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found", ErrorCodes.RESOURCE_NOT_FOUND));

        String oldShortCode = shortUrl.getShortCode();
        String updatedOriginalUrl = request.getOriginalUrl() == null ? shortUrl.getOriginalUrl() : normalizeAndValidateUrl(request.getOriginalUrl());
        String requestedAlias = request.getCustomAlias() == null ? null : normalizeAlias(request.getCustomAlias());

        if (request.getExpiresAt() != null) {
            validateExpiry(request.getExpiresAt());
            shortUrl.setExpiresAt(request.getExpiresAt());
        }

        if (requestedAlias != null && !requestedAlias.equals(oldShortCode)) {
            if (shortUrlRepository.existsByShortCode(requestedAlias)) {
                throw new ConflictException("Custom alias already exists", ErrorCodes.CONFLICT);
            }
            shortUrl.setShortCode(requestedAlias);
            shortUrl.setCustomAlias(requestedAlias);
        } else if (requestedAlias == null && request.getCustomAlias() != null && request.getCustomAlias().isBlank()) {
            shortUrl.setCustomAlias(null);
        }

        shortUrl.setOriginalUrl(updatedOriginalUrl);
        shortUrl.setUpdatedAt(Instant.now());

        ShortUrl savedShortUrl = shortUrlRepository.save(shortUrl);
        shortUrlCacheService.evict(oldShortCode);
        cacheShortUrl(savedShortUrl);
        return ShortUrlMapper.toResponse(savedShortUrl, shortUrlProperties);
    }

    @Override
    @Transactional
    public ShortUrlResponse updateShortUrlStatus(String createdByEmail, Long shortUrlId, UpdateShortUrlStatusRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(createdByEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCodes.RESOURCE_NOT_FOUND));

        ShortUrl shortUrl = shortUrlRepository.findByIdAndCreatedByEmail(shortUrlId, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found", ErrorCodes.RESOURCE_NOT_FOUND));

        shortUrl.setEnabled(request.isEnabled());
        shortUrl.setUpdatedAt(Instant.now());
        ShortUrl savedShortUrl = shortUrlRepository.save(shortUrl);
        if (request.isEnabled()) {
            cacheShortUrl(savedShortUrl);
        } else {
            shortUrlCacheService.evict(savedShortUrl.getShortCode());
        }
        return ShortUrlMapper.toResponse(savedShortUrl, shortUrlProperties);
    }

    @Override
    @Transactional
    public void deleteShortUrl(String createdByEmail, Long shortUrlId) {
        User user = userRepository.findByEmail(normalizeEmail(createdByEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCodes.RESOURCE_NOT_FOUND));

        ShortUrl shortUrl = shortUrlRepository.findByIdAndCreatedByEmail(shortUrlId, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found", ErrorCodes.RESOURCE_NOT_FOUND));

        shortUrlCacheService.evict(shortUrl.getShortCode());
        shortUrlRepository.delete(shortUrl);
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

    private Pageable toPageable(int page, int size, String sortBy, String sortDirection) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        String safeSortBy = normalizeSortField(sortBy);
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(safePage, safeSize, Sort.by(direction, safeSortBy));
    }

    private String normalizeSortField(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }
        String candidate = sortBy.trim();
        if (!ALLOWED_SORT_FIELDS.contains(candidate)) {
            throw new BadRequestException("Unsupported sort field", ErrorCodes.BAD_REQUEST);
        }
        return candidate;
    }
}
