package com.project.urlshortener.service.impl;

import com.project.urlshortener.repository.ClickEventRepository;
import com.project.urlshortener.repository.RefreshTokenRepository;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ClickEventRepository clickEventRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private ReportingServiceImpl reportingService;

    @BeforeEach
    void setUp() {
        reportingService = new ReportingServiceImpl(userRepository, shortUrlRepository, clickEventRepository, refreshTokenRepository);
    }

    @Test
    void getOperationsReportShouldAggregateRepositoryCounts() {
        when(userRepository.count()).thenReturn(3L);
        when(shortUrlRepository.count()).thenReturn(8L);
        when(shortUrlRepository.countActiveShortUrls(any(Instant.class))).thenReturn(5L);
        when(shortUrlRepository.countExpiredShortUrls(any(Instant.class))).thenReturn(2L);
        when(clickEventRepository.count()).thenReturn(42L);
        when(refreshTokenRepository.count()).thenReturn(7L);
        when(refreshTokenRepository.countByRevokedFalseAndExpiresAtAfter(any(Instant.class))).thenReturn(4L);
        when(refreshTokenRepository.countByRevokedTrue()).thenReturn(2L);
        when(refreshTokenRepository.countByExpiresAtBefore(any(Instant.class))).thenReturn(1L);

        var report = reportingService.getOperationsReport();

        assertEquals(3L, report.getTotalUsers());
        assertEquals(8L, report.getTotalShortUrls());
        assertEquals(5L, report.getActiveShortUrls());
        assertEquals(2L, report.getExpiredShortUrls());
        assertEquals(42L, report.getTotalClickEvents());
        assertEquals(7L, report.getTotalRefreshTokens());
        assertEquals(4L, report.getActiveRefreshTokens());
        assertEquals(2L, report.getRevokedRefreshTokens());
        assertEquals(1L, report.getExpiredRefreshTokens());
    }
}
