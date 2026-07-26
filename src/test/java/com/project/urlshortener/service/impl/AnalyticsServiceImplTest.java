package com.project.urlshortener.service.impl;

import com.project.urlshortener.cache.ShortUrlCacheEntry;
import com.project.urlshortener.cache.ShortUrlCacheService;
import com.project.urlshortener.dto.response.analytics.AnalyticsDashboardResponse;
import com.project.urlshortener.entity.ClickEvent;
import com.project.urlshortener.entity.ShortUrl;
import com.project.urlshortener.entity.User;
import com.project.urlshortener.entity.UserRole;
import com.project.urlshortener.repository.ClickEventRepository;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.util.RequestMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private ClickEventRepository clickEventRepository;

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private RequestMetadataExtractor requestMetadataExtractor;

    @Mock
    private ShortUrlCacheService shortUrlCacheService;

    @Mock
    private HttpServletRequest request;

    private AnalyticsServiceImpl analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsServiceImpl(clickEventRepository, shortUrlRepository, requestMetadataExtractor, shortUrlCacheService);
    }

    @Test
    void recordClickShouldPersistEventWithMetadata() {
        ShortUrl shortUrl = sampleShortUrl();
        when(shortUrlRepository.findByShortCode("springtips")).thenReturn(Optional.of(shortUrl));
        when(shortUrlCacheService.get("springtips")).thenReturn(Optional.of(new ShortUrlCacheEntry("https://example.com", true, null)));
        when(requestMetadataExtractor.resolveIpAddress(request)).thenReturn("127.0.0.1");
        when(requestMetadataExtractor.resolveCountry(request)).thenReturn("India");
        when(requestMetadataExtractor.resolveCity(request)).thenReturn("Pune");
        when(requestMetadataExtractor.resolveBrowser(request)).thenReturn("Chrome");
        when(requestMetadataExtractor.resolveOperatingSystem(request)).thenReturn("Windows");
        when(requestMetadataExtractor.resolveDevice(request)).thenReturn("Desktop");
        when(requestMetadataExtractor.resolveReferral(request)).thenReturn("Direct");

        analyticsService.recordClick("springtips", request);

        verify(clickEventRepository).save(any(ClickEvent.class));
    }

    @Test
    void dashboardShouldAggregateEvents() {
        List<ClickEvent> events = List.of(sampleClickEvent("springtips", "India", "Chrome", "Desktop", "user@example.com"),
                sampleClickEvent("springtips", "India", "Chrome", "Desktop", "user@example.com"),
                sampleClickEvent("springtips2", "US", "Firefox", "Mobile", "admin@example.com"));
        when(clickEventRepository.findByClickedAtBetweenOrderByClickedAtAsc(any(Instant.class), any(Instant.class))).thenReturn(events);

        AnalyticsDashboardResponse dashboard = analyticsService.getDashboard(30, 5);

        assertEquals(3L, dashboard.getTotalClicks());
        assertEquals(2L, dashboard.getUniqueShortUrls());
        assertEquals(2L, dashboard.getTopCountries().size());
        assertEquals("India", dashboard.getTopCountries().get(0).getName());
    }

    private ShortUrl sampleShortUrl() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setShortCode("springtips");
        shortUrl.setOriginalUrl("https://example.com");
        shortUrl.setEnabled(true);
        shortUrl.setCreatedBy(sampleUser("user@example.com"));
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setUpdatedAt(Instant.now());
        return shortUrl;
    }

    private ClickEvent sampleClickEvent(String shortCode, String country, String browser, String device, String email) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setShortCode(shortCode);
        shortUrl.setOriginalUrl("https://example.com/" + shortCode);
        shortUrl.setEnabled(true);
        shortUrl.setCreatedBy(sampleUser(email));
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setUpdatedAt(Instant.now());

        ClickEvent event = new ClickEvent();
        event.setShortUrl(shortUrl);
        event.setCreatedBy(sampleUser(email));
        event.setCountry(country);
        event.setBrowser(browser);
        event.setDevice(device);
        event.setClickedAt(Instant.now());
        return event;
    }

    private User sampleUser(String email) {
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("encoded");
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }
}
