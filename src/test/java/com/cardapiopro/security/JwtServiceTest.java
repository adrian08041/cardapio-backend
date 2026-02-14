package com.cardapiopro.security;

import com.cardapiopro.entity.User;
import com.cardapiopro.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("generateAccessToken")
    class GenerateAccessToken {

        @Test
        @DisplayName("Deve gerar access token válido")
        void shouldGenerateValidAccessToken() {
            String token = jwtService.generateAccessToken(testUser);

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT tem 3 partes
        }

        @Test
        @DisplayName("Access token deve conter email do usuário")
        void accessTokenShouldContainUserEmail() {
            String token = jwtService.generateAccessToken(testUser);
            String email = jwtService.extractEmail(token);

            assertThat(email).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("generateRefreshToken")
    class GenerateRefreshToken {

        @Test
        @DisplayName("Deve gerar refresh token válido")
        void shouldGenerateValidRefreshToken() {
            String token = jwtService.generateRefreshToken(testUser);

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("Refresh token deve ser diferente do access token")
        void refreshTokenShouldBeDifferentFromAccessToken() {
            String accessToken = jwtService.generateAccessToken(testUser);
            String refreshToken = jwtService.generateRefreshToken(testUser);

            assertThat(refreshToken).isNotEqualTo(accessToken);
        }
    }

    @Nested
    @DisplayName("extractEmail")
    class ExtractEmail {

        @Test
        @DisplayName("Deve extrair email do token corretamente")
        void shouldExtractEmailFromToken() {
            String token = jwtService.generateAccessToken(testUser);

            String email = jwtService.extractEmail(token);

            assertThat(email).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("isTokenValid")
    class IsTokenValid {

        @Test
        @DisplayName("Deve validar token correto")
        void shouldValidateCorrectToken() {
            String token = jwtService.generateAccessToken(testUser);

            boolean isValid = jwtService.isTokenValid(token, testUser);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false para usuário diferente")
        void shouldReturnFalseForDifferentUser() {
            String token = jwtService.generateAccessToken(testUser);

            User anotherUser = User.builder()
                    .id(UUID.randomUUID())
                    .name("Another User")
                    .email("another@example.com")
                    .password("password")
                    .role(UserRole.CUSTOMER)
                    .build();

            boolean isValid = jwtService.isTokenValid(token, anotherUser);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Deve validar token com mesmo email")
        void shouldValidateTokenWithSameEmail() {
            String token = jwtService.generateAccessToken(testUser);

            User sameEmailUser = User.builder()
                    .id(UUID.randomUUID())
                    .name("Same Email User")
                    .email("test@example.com")
                    .password("password")
                    .role(UserRole.ADMIN)
                    .build();

            boolean isValid = jwtService.isTokenValid(token, sameEmailUser);

            assertThat(isValid).isTrue();
        }
    }

    @Nested
    @DisplayName("getAccessTokenExpiration")
    class GetAccessTokenExpiration {

        @Test
        @DisplayName("Deve retornar expiração em segundos")
        void shouldReturnExpirationInSeconds() {
            long expiration = jwtService.getAccessTokenExpiration();

            assertThat(expiration).isEqualTo(3600L); // 3600000ms / 1000 = 3600s
        }
    }
}
