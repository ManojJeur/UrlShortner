package com.project.urlshortener.dto.response;

import java.util.Objects;

public class AuthResponse {

    private UserResponse user;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresInSeconds;

    public AuthResponse() {
    }

    public AuthResponse(UserResponse user, String accessToken, String refreshToken, String tokenType, long expiresInSeconds) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthResponse that = (AuthResponse) o;
        return expiresInSeconds == that.expiresInSeconds
                && Objects.equals(user, that.user)
                && Objects.equals(accessToken, that.accessToken)
                && Objects.equals(refreshToken, that.refreshToken)
                && Objects.equals(tokenType, that.tokenType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, accessToken, refreshToken, tokenType, expiresInSeconds);
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "user=" + user +
                ", accessToken='[PROTECTED]'" +
                ", refreshToken='[PROTECTED]'" +
                ", tokenType='" + tokenType + '\'' +
                ", expiresInSeconds=" + expiresInSeconds +
                '}';
    }
}
