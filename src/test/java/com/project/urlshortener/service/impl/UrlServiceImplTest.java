package com.project.urlshortener.service.impl;

import com.project.urlshortener.cache.ShortUrlCacheService;
import com.project.urlshortener.cache.ShortUrlCacheEntry;
import com.project.urlshortener.config.RedisAppProperties;
import com.project.urlshortener.config.ShortUrlProperties;
import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.dto.request.ShortUrlSearchRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlStatusRequest;
import com.project.urlshortener.dto.response.PageResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
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

    @Test
    void listMyShortUrlsShouldReturnPagedResults() {
        ShortUrl shortUrl = sampleShortUrl("springtips");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser()));
        when(shortUrlRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(shortUrl)));

        PageResponse<?> response = urlService.listMyShortUrls("user@example.com",
                new ShortUrlSearchRequest("spring", true, false, true, 0, 20, "createdAt", "DESC"));

        assertEquals(1L, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals("springtips", ((com.project.urlshortener.dto.response.ShortUrlResponse) response.getContent().get(0)).getShortCode());
    }

    @Test
    void updateShortUrlShouldChangeAliasAndInvalidateCache() {
        ShortUrl existing = sampleShortUrl("springtips");
        existing.setId(1L);
        existing.setCustomAlias("springtips");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser()));
        when(shortUrlRepository.findByIdAndCreatedByEmail(1L, "user@example.com")).thenReturn(Optional.of(existing));
        when(shortUrlRepository.existsByShortCode("springnews")).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = urlService.updateShortUrl("user@example.com", 1L,
                new UpdateShortUrlRequest("https://example.com/articles/new", "SpringNews", Instant.parse("2026-08-01T00:00:00Z")));

        assertEquals("springnews", response.getShortCode());
        verify(shortUrlCacheService).evict("springtips");
    }

    @Test
    void updateShortUrlStatusShouldDisableAndEvictCache() {
        ShortUrl existing = sampleShortUrl("springtips");
        existing.setId(1L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser()));
        when(shortUrlRepository.findByIdAndCreatedByEmail(1L, "user@example.com")).thenReturn(Optional.of(existing));
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = urlService.updateShortUrlStatus("user@example.com", 1L, new UpdateShortUrlStatusRequest(false));

        assertEquals(false, response.isEnabled());
        verify(shortUrlCacheService).evict("springtips");
    }

    @Test
    void deleteShortUrlShouldRemoveOwnedRecord() {
        ShortUrl existing = sampleShortUrl("springtips");
        existing.setId(1L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser()));
        when(shortUrlRepository.findByIdAndCreatedByEmail(1L, "user@example.com")).thenReturn(Optional.of(existing));

        urlService.deleteShortUrl("user@example.com", 1L);

        verify(shortUrlCacheService).evict("springtips");
        verify(shortUrlRepository).delete(existing);
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

    private ShortUrl sampleShortUrl(String shortCode) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setShortCode(shortCode);
        shortUrl.setCustomAlias(shortCode);
        shortUrl.setOriginalUrl("https://example.com/articles/spring-boot");
        shortUrl.setEnabled(true);
        shortUrl.setCreatedBy(sampleUser());
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setUpdatedAt(Instant.now());
        return shortUrl;
    }
}
