package com.project.urlshortener.repository;

import com.project.urlshortener.entity.ShortUrl;
import com.project.urlshortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long>, JpaSpecificationExecutor<ShortUrl> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<ShortUrl> findAllByCreatedByOrderByCreatedAtDesc(User createdBy);

    Optional<ShortUrl> findByIdAndCreatedByEmail(Long id, String email);

    @Query("select count(s) from ShortUrl s where s.enabled = true and (s.expiresAt is null or s.expiresAt > :now)")
    long countActiveShortUrls(@Param("now") java.time.Instant now);

    @Query("select count(s) from ShortUrl s where s.enabled = true and s.expiresAt is not null and s.expiresAt <= :now")
    long countExpiredShortUrls(@Param("now") java.time.Instant now);

    @Modifying
    @Query("update ShortUrl s set s.clickCount = s.clickCount + 1, s.updatedAt = :updatedAt where s.shortCode = :shortCode")
    int incrementClickCountByShortCode(@Param("shortCode") String shortCode, @Param("updatedAt") java.time.Instant updatedAt);
}
