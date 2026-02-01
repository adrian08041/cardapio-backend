package com.cardapiopro.service;

import com.cardapiopro.dto.request.LoginRequest;
import com.cardapiopro.dto.request.RegisterRequest;
import com.cardapiopro.dto.response.AuthResponse;
import com.cardapiopro.entity.User;
import com.cardapiopro.entity.enums.UserRole;
import com.cardapiopro.repository.UserRepository;
import com.cardapiopro.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("encoded_password")
                .role(UserRole.CUSTOMER)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void register_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123", null);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access_token");
        assertThat(response.refreshToken()).isEqualTo("refresh_token");
        assertThat(response.tokenType()).isEqualTo("Bearer");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_EmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123", null);

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully")
    void login_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser, null));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access_token");
        assertThat(response.user().email()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should register with specified role")
    void register_WithSpecificRole() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Admin User", "admin@example.com", "password123", UserRole.ADMIN);

        User adminUser = User.builder()
                .id(UUID.randomUUID())
                .name("Admin User")
                .email("admin@example.com")
                .password("encoded_password")
                .role(UserRole.ADMIN)
                .active(true)
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response.user().role()).isEqualTo(UserRole.ADMIN);
    }
}
