package com.project.urlshortener.dto.response;

import java.time.Instant;
import java.util.Objects;

public class UserResponse {

    private Long id;
    private String email;
    private String role;
    private boolean enabled;
    private Instant createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, String role, boolean enabled, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        UserResponse that = (UserResponse) o;
        return enabled == that.enabled
                && Objects.equals(id, that.id)
                && Objects.equals(email, that.email)
                && Objects.equals(role, that.role)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, role, enabled, createdAt);
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                '}';
    }
}
