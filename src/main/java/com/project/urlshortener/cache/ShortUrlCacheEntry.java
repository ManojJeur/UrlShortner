package com.project.urlshortener.cache;

import java.time.Instant;
import java.util.Objects;

public class ShortUrlCacheEntry {

    private String originalUrl;
    private boolean enabled;
    private Instant expiresAt;

    public ShortUrlCacheEntry() {
    }

    public ShortUrlCacheEntry(String originalUrl, boolean enabled, Instant expiresAt) {
        this.originalUrl = originalUrl;
        this.enabled = enabled;
        this.expiresAt = expiresAt;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShortUrlCacheEntry that = (ShortUrlCacheEntry) o;
        return enabled == that.enabled
                && Objects.equals(originalUrl, that.originalUrl)
                && Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalUrl, enabled, expiresAt);
    }

    @Override
    public String toString() {
        return "ShortUrlCacheEntry{" +
                "originalUrl='" + originalUrl + '\'' +
                ", enabled=" + enabled +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
