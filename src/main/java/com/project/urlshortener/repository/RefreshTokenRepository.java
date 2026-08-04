package com.project.urlshortener.repository;

import com.project.urlshortener.entity.RefreshToken;
import com.project.urlshortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    long deleteByExpiresAtBefore(Instant instant);

    long deleteByUser(User user);

    long countByRevokedFalseAndExpiresAtAfter(Instant instant);

    long countByRevokedTrue();

    long countByExpiresAtBefore(Instant instant);
}
