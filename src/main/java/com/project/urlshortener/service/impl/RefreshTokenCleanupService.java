package com.project.urlshortener.service.impl;

import com.project.urlshortener.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshTokenCleanupService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleanupService.class);

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "${app.maintenance.refresh-token-cleanup-cron:0 0 2 * * *}", zone = "${app.timezone:UTC}")
    @Transactional
    public void purgeExpiredRefreshTokens() {
        long deletedCount = refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        if (deletedCount > 0) {
            log.info("Purged {} expired refresh tokens", deletedCount);
        }
    }
}
