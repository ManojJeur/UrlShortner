package com.project.urlshortener.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.redis")
public class RedisAppProperties {

    private long urlCacheTtlSeconds = 3600;
    private int authRateLimit = 10;
    private int authRateLimitWindowSeconds = 60;
    private int urlCreateRateLimit = 30;
    private int urlCreateRateLimitWindowSeconds = 60;
    private int redirectRateLimit = 120;
    private int redirectRateLimitWindowSeconds = 60;

    public RedisAppProperties() {
    }

    public long getUrlCacheTtlSeconds() {
        return urlCacheTtlSeconds;
    }

    public void setUrlCacheTtlSeconds(long urlCacheTtlSeconds) {
        this.urlCacheTtlSeconds = urlCacheTtlSeconds;
    }

    public int getAuthRateLimit() {
        return authRateLimit;
    }

    public void setAuthRateLimit(int authRateLimit) {
        this.authRateLimit = authRateLimit;
    }

    public int getAuthRateLimitWindowSeconds() {
        return authRateLimitWindowSeconds;
    }

    public void setAuthRateLimitWindowSeconds(int authRateLimitWindowSeconds) {
        this.authRateLimitWindowSeconds = authRateLimitWindowSeconds;
    }

    public int getUrlCreateRateLimit() {
        return urlCreateRateLimit;
    }

    public void setUrlCreateRateLimit(int urlCreateRateLimit) {
        this.urlCreateRateLimit = urlCreateRateLimit;
    }

    public int getUrlCreateRateLimitWindowSeconds() {
        return urlCreateRateLimitWindowSeconds;
    }

    public void setUrlCreateRateLimitWindowSeconds(int urlCreateRateLimitWindowSeconds) {
        this.urlCreateRateLimitWindowSeconds = urlCreateRateLimitWindowSeconds;
    }

    public int getRedirectRateLimit() {
        return redirectRateLimit;
    }

    public void setRedirectRateLimit(int redirectRateLimit) {
        this.redirectRateLimit = redirectRateLimit;
    }

    public int getRedirectRateLimitWindowSeconds() {
        return redirectRateLimitWindowSeconds;
    }

    public void setRedirectRateLimitWindowSeconds(int redirectRateLimitWindowSeconds) {
        this.redirectRateLimitWindowSeconds = redirectRateLimitWindowSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RedisAppProperties that = (RedisAppProperties) o;
        return urlCacheTtlSeconds == that.urlCacheTtlSeconds
                && authRateLimit == that.authRateLimit
                && authRateLimitWindowSeconds == that.authRateLimitWindowSeconds
                && urlCreateRateLimit == that.urlCreateRateLimit
                && urlCreateRateLimitWindowSeconds == that.urlCreateRateLimitWindowSeconds
                && redirectRateLimit == that.redirectRateLimit
                && redirectRateLimitWindowSeconds == that.redirectRateLimitWindowSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(urlCacheTtlSeconds, authRateLimit, authRateLimitWindowSeconds, urlCreateRateLimit, urlCreateRateLimitWindowSeconds, redirectRateLimit, redirectRateLimitWindowSeconds);
    }

    @Override
    public String toString() {
        return "RedisAppProperties{" +
                "urlCacheTtlSeconds=" + urlCacheTtlSeconds +
                ", authRateLimit=" + authRateLimit +
                ", authRateLimitWindowSeconds=" + authRateLimitWindowSeconds +
                ", urlCreateRateLimit=" + urlCreateRateLimit +
                ", urlCreateRateLimitWindowSeconds=" + urlCreateRateLimitWindowSeconds +
                ", redirectRateLimit=" + redirectRateLimit +
                ", redirectRateLimitWindowSeconds=" + redirectRateLimitWindowSeconds +
                '}';
    }
}
