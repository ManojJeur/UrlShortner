package com.project.urlshortener.service.impl;

import com.project.urlshortener.cache.ShortUrlCacheService;
import com.project.urlshortener.cache.ShortUrlCacheEntry;
import com.project.urlshortener.config.RedisAppProperties;
import com.project.urlshortener.config.ShortUrlProperties;
import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.entity.ShortUrl;
import com.project.urlshortener.entity.User;
import com.project.urlshortener.entity.UserRole;
import com.project.urlshortener.exception.ConflictException;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.repository.UserRepository;
import com.project.urlshortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private ShortUrlCacheService shortUrlCacheService;

    private ShortUrlProperties shortUrlProperties;
    private RedisAppProperties redisAppProperties;

    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        shortUrlProperties = new ShortUrlProperties();
        shortUrlProperties.setBaseUrl("http://localhost:8080");
        shortUrlProperties.setCodeLength(7);
        shortUrlProperties.setGenerationAttempts(5);
        redisAppProperties = new RedisAppProperties();
        redisAppProperties.setUrlCacheTtlSeconds(3600L);
        urlService = new UrlServiceImpl(shortUrlRepository, userRepository, shortCodeGenerator, shortUrlProperties, redisAppProperties, shortUrlCacheService);
        when(shortUrlCacheService.get(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void createShortUrlShouldPersistCustomAlias() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser()));
        when(shortUrlRepository.existsByShortCode("springtips")).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> {
            ShortUrl shortUrl = invocation.getArgument(0);
            shortUrl.setId(1L);
            return shortUrl;
        });

        var response = urlService.createShortUrl("user@example.com",
                new CreateShortUrlRequest("https://example.com/articles/spring-boot", "SpringTips", Instant.parse("2026-08-01T00:00:00Z")));

        assertEquals("springtips", response.getShortCode());
        assertEquals("http://localhost:8080/api/v1/r/springtips", response.getShortUrl());
        verify(shortUrlRepository).save(any(ShortUrl.class));
        verify(shortUrlCacheService).put(eq("springtips"), any(ShortUrlCacheEntry.class), any(Duration.class));
    }

    @Test
    void createShortUrlShouldRejectDuplicateAlias() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser()));
        when(shortUrlRepository.existsByShortCode("springtips")).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                urlService.createShortUrl("user@example.com",
                        new CreateShortUrlRequest("https://example.com/articles/spring-boot", "SpringTips", null)));
    }

    @Test
    void resolveOriginalUrlShouldIncrementClickCount() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setShortCode("springtips");
        shortUrl.setOriginalUrl("https://example.com/articles/spring-boot");
        shortUrl.setEnabled(true);
        shortUrl.setClickCount(3L);
        shortUrl.setCreatedBy(sampleUser());
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setUpdatedAt(Instant.now());

        when(shortUrlRepository.findByShortCode("springtips")).thenReturn(Optional.of(shortUrl));

        String originalUrl = urlService.resolveOriginalUrl("springtips");

        assertEquals("https://example.com/articles/spring-boot", originalUrl);
        verify(shortUrlRepository).incrementClickCountByShortCode(eq("springtips"), any(Instant.class));
    }

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }
}
