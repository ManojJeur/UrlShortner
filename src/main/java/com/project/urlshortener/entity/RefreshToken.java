package com.project.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public RefreshToken() {
    }

    public RefreshToken(Long id, String tokenHash, User user, Instant expiresAt, boolean revoked, Instant createdAt) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
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
        RefreshToken that = (RefreshToken) o;
        return revoked == that.revoked
                && Objects.equals(id, that.id)
                && Objects.equals(tokenHash, that.tokenHash)
                && Objects.equals(user, that.user)
                && Objects.equals(expiresAt, that.expiresAt)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tokenHash, user, expiresAt, revoked, createdAt);
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", tokenHash='[PROTECTED]'" +
                ", userId=" + (user != null ? user.getId() : null) +
                ", expiresAt=" + expiresAt +
                ", revoked=" + revoked +
                ", createdAt=" + createdAt +
                '}';
    }
}
