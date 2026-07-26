package com.project.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenSeconds = 900;
    private long refreshTokenSeconds = 1209600;

    public JwtProperties() {
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenSeconds() {
        return accessTokenSeconds;
    }

    public void setAccessTokenSeconds(long accessTokenSeconds) {
        this.accessTokenSeconds = accessTokenSeconds;
    }

    public long getRefreshTokenSeconds() {
        return refreshTokenSeconds;
    }

    public void setRefreshTokenSeconds(long refreshTokenSeconds) {
        this.refreshTokenSeconds = refreshTokenSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JwtProperties that = (JwtProperties) o;
        return accessTokenSeconds == that.accessTokenSeconds
                && refreshTokenSeconds == that.refreshTokenSeconds
                && Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secret, accessTokenSeconds, refreshTokenSeconds);
    }

    @Override
    public String toString() {
        return "JwtProperties{" +
                "secret='[PROTECTED]'" +
                ", accessTokenSeconds=" + accessTokenSeconds +
                ", refreshTokenSeconds=" + refreshTokenSeconds +
                '}';
    }
}
