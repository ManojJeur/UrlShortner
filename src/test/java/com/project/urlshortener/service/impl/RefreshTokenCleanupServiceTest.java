package com.project.urlshortener.service.impl;

import com.project.urlshortener.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCleanupServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenCleanupService refreshTokenCleanupService;

    @BeforeEach
    void setUp() {
        refreshTokenCleanupService = new RefreshTokenCleanupService(refreshTokenRepository);
    }

    @Test
    void purgeExpiredRefreshTokensShouldDeleteOldTokens() {
        when(refreshTokenRepository.deleteByExpiresAtBefore(any(Instant.class))).thenReturn(4L);

        refreshTokenCleanupService.purgeExpiredRefreshTokens();

        verify(refreshTokenRepository).deleteByExpiresAtBefore(any(Instant.class));
    }
}
