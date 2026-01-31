package com.cardapiopro.service;

import com.cardapiopro.dto.request.LoginRequest;
import com.cardapiopro.dto.request.RefreshTokenRequest;
import com.cardapiopro.dto.request.RegisterRequest;
import com.cardapiopro.dto.response.AuthResponse;
import com.cardapiopro.entity.User;
import com.cardapiopro.entity.enums.UserRole;
import com.cardapiopro.repository.UserRepository;
import com.cardapiopro.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já cadastrado: " + request.email());
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : UserRole.CUSTOMER)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getAccessTokenExpiration(), user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getAccessTokenExpiration(), user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String email = jwtService.extractEmail(request.refreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new IllegalArgumentException("Refresh token expirado ou inválido");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getAccessTokenExpiration(), user);
    }
}
