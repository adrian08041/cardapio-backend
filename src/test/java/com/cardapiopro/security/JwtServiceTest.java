package com.cardapiopro.security;

import com.cardapiopro.entity.User;
import com.cardapiopro.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "test-secret-key-for-testing-purposes-256-bits-minimum");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 604800000L);

        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .role(UserRole.CUSTOMER)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should generate access token")
    void generateAccessToken_Success() {
        // Act
        String token = jwtService.generateAccessToken(testUser);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should generate refresh token")
    void generateRefreshToken_Success() {
        // Act
        String token = jwtService.generateRefreshToken(testUser);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Should extract email from token")
    void extractEmail_Success() {
        // Arrange
        String token = jwtService.generateAccessToken(testUser);

        // Act
        String email = jwtService.extractEmail(token);

        // Assert
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should validate token")
    void isTokenValid_ValidToken() {
        // Arrange
        String token = jwtService.generateAccessToken(testUser);

        // Act
        boolean isValid = jwtService.isTokenValid(token, testUser);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false for invalid user")
    void isTokenValid_WrongUser() {
        // Arrange
        String token = jwtService.generateAccessToken(testUser);

        User anotherUser = User.builder()
                .id(UUID.randomUUID())
                .name("Another User")
                .email("another@example.com")
                .password("password")
                .role(UserRole.CUSTOMER)
                .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should get access token expiration in seconds")
    void getAccessTokenExpiration_Success() {
        // Act
        long expiration = jwtService.getAccessTokenExpiration();

        // Assert
        assertThat(expiration).isEqualTo(3600); // 3600000ms / 1000 = 3600s
    }
}
