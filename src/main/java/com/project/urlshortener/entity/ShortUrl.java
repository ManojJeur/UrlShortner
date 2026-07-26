package com.project.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "short_urls",
        indexes = {
                @Index(name = "idx_short_urls_short_code", columnList = "short_code"),
                @Index(name = "idx_short_urls_created_by_id", columnList = "created_by_id"),
                @Index(name = "idx_short_urls_expires_at", columnList = "expires_at")
        }
)
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "short_code", nullable = false, unique = true, length = 32)
    private String shortCode;

    @Column(name = "custom_alias", length = 32)
    private String customAlias;

    @Column(name = "click_count", nullable = false)
    private long clickCount;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ShortUrl() {
    }

    public ShortUrl(Long id, String originalUrl, String shortCode, String customAlias, long clickCount, boolean enabled, Instant expiresAt, User createdBy, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.customAlias = customAlias;
        this.clickCount = clickCount;
        this.enabled = enabled;
        this.expiresAt = expiresAt;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShortUrl shortUrl = (ShortUrl) o;
        return clickCount == shortUrl.clickCount
                && enabled == shortUrl.enabled
                && Objects.equals(id, shortUrl.id)
                && Objects.equals(originalUrl, shortUrl.originalUrl)
                && Objects.equals(shortCode, shortUrl.shortCode)
                && Objects.equals(customAlias, shortUrl.customAlias)
                && Objects.equals(expiresAt, shortUrl.expiresAt)
                && Objects.equals(createdBy, shortUrl.createdBy)
                && Objects.equals(createdAt, shortUrl.createdAt)
                && Objects.equals(updatedAt, shortUrl.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originalUrl, shortCode, customAlias, clickCount, enabled, expiresAt, createdBy, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "ShortUrl{" +
                "id=" + id +
                ", originalUrl='" + originalUrl + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", customAlias='" + customAlias + '\'' +
                ", clickCount=" + clickCount +
                ", enabled=" + enabled +
                ", expiresAt=" + expiresAt +
                ", createdById=" + (createdBy != null ? createdBy.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
