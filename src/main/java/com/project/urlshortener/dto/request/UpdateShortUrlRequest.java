package com.project.urlshortener.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Objects;

public class UpdateShortUrlRequest {

    @Size(max = 2048, message = "Original URL must not exceed 2048 characters")
    private String originalUrl;

    @Size(max = 32, message = "Custom alias must not exceed 32 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]*$",
            message = "Custom alias can contain only letters, numbers, underscores, and hyphens"
    )
    private String customAlias;

    private Instant expiresAt;

    public UpdateShortUrlRequest() {
    }

    public UpdateShortUrlRequest(String originalUrl, String customAlias, Instant expiresAt) {
        this.originalUrl = originalUrl;
        this.customAlias = customAlias;
        this.expiresAt = expiresAt;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
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
        UpdateShortUrlRequest that = (UpdateShortUrlRequest) o;
        return Objects.equals(originalUrl, that.originalUrl)
                && Objects.equals(customAlias, that.customAlias)
                && Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalUrl, customAlias, expiresAt);
    }

    @Override
    public String toString() {
        return "UpdateShortUrlRequest{" +
                "originalUrl='" + originalUrl + '\'' +
                ", customAlias='" + customAlias + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
