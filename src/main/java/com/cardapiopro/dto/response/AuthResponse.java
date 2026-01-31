package com.cardapiopro.dto.response;

import com.cardapiopro.entity.User;
import com.cardapiopro.entity.enums.UserRole;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserInfo user) {
    public record UserInfo(
            UUID id,
            String name,
            String email,
            UserRole role) {
    }

    public static AuthResponse of(String accessToken, String refreshToken, Long expiresIn, User user) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                new UserInfo(user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }
}
