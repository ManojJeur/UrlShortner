package com.project.urlshortener.service.impl;

import com.project.urlshortener.dto.response.report.OperationsReportResponse;
import com.project.urlshortener.repository.ClickEventRepository;
import com.project.urlshortener.repository.RefreshTokenRepository;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.repository.UserRepository;
import com.project.urlshortener.service.ReportingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final UserRepository userRepository;
    private final ShortUrlRepository shortUrlRepository;
    private final ClickEventRepository clickEventRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public ReportingServiceImpl(UserRepository userRepository,
                                ShortUrlRepository shortUrlRepository,
                                ClickEventRepository clickEventRepository,
                                RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.shortUrlRepository = shortUrlRepository;
        this.clickEventRepository = clickEventRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public OperationsReportResponse getOperationsReport() {
        Instant now = Instant.now();
        long totalRefreshTokens = refreshTokenRepository.count();
        long activeRefreshTokens = refreshTokenRepository.countByRevokedFalseAndExpiresAtAfter(now);
        long revokedRefreshTokens = refreshTokenRepository.countByRevokedTrue();
        long expiredRefreshTokens = refreshTokenRepository.countByExpiresAtBefore(now);

        return new OperationsReportResponse(
                now,
                userRepository.count(),
                shortUrlRepository.count(),
                shortUrlRepository.countActiveShortUrls(now),
                shortUrlRepository.countExpiredShortUrls(now),
                clickEventRepository.count(),
                totalRefreshTokens,
                activeRefreshTokens,
                revokedRefreshTokens,
                expiredRefreshTokens
        );
    }
}
