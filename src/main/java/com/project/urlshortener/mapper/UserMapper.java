package com.project.urlshortener.mapper;

import com.project.urlshortener.dto.response.UserResponse;
import com.project.urlshortener.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
