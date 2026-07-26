package com.project.urlshortener.dto.response;

import java.time.Instant;
import java.util.Objects;

public class ShortUrlResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private String customAlias;
    private long clickCount;
    private boolean enabled;
    private Instant expiresAt;
    private Instant createdAt;

    public ShortUrlResponse() {
    }

    public ShortUrlResponse(Long id, String originalUrl, String shortCode, String shortUrl, String customAlias, long clickCount, boolean enabled, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.customAlias = customAlias;
        this.clickCount = clickCount;
        this.enabled = enabled;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
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

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShortUrlResponse that = (ShortUrlResponse) o;
        return clickCount == that.clickCount
                && enabled == that.enabled
                && Objects.equals(id, that.id)
                && Objects.equals(originalUrl, that.originalUrl)
                && Objects.equals(shortCode, that.shortCode)
                && Objects.equals(shortUrl, that.shortUrl)
                && Objects.equals(customAlias, that.customAlias)
                && Objects.equals(expiresAt, that.expiresAt)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originalUrl, shortCode, shortUrl, customAlias, clickCount, enabled, expiresAt, createdAt);
    }

    @Override
    public String toString() {
        return "ShortUrlResponse{" +
                "id=" + id +
                ", originalUrl='" + originalUrl + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", customAlias='" + customAlias + '\'' +
                ", clickCount=" + clickCount +
                ", enabled=" + enabled +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
