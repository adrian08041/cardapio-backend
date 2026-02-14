package com.cardapiopro.controller;

import com.cardapiopro.dto.request.LoginRequest;
import com.cardapiopro.dto.request.RegisterRequest;
import com.cardapiopro.entity.enums.UserRole;
import com.cardapiopro.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class Register {

        @Test
        @DisplayName("Deve registrar novo usuário com sucesso")
        void shouldRegisterNewUser() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "Test User",
                    "test@example.com",
                    "password123",
                    null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresIn").exists())
                    .andExpect(jsonPath("$.user.email").value("test@example.com"))
                    .andExpect(jsonPath("$.user.name").value("Test User"))
                    .andExpect(jsonPath("$.user.role").value("CUSTOMER"));
        }

        @Test
        @DisplayName("Deve registrar usuário com role específica")
        void shouldRegisterUserWithSpecificRole() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "Admin User",
                    "admin@example.com",
                    "password123",
                    UserRole.ADMIN);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.user.role").value("ADMIN"));
        }

        @Test
        @DisplayName("Deve falhar ao registrar com email já existente")
        void shouldFailToRegisterWithExistingEmail() throws Exception {
            RegisterRequest firstRequest = new RegisterRequest(
                    "First User",
                    "duplicate@example.com",
                    "password123",
                    null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(firstRequest)))
                    .andExpect(status().isCreated());

            RegisterRequest duplicateRequest = new RegisterRequest(
                    "Second User",
                    "duplicate@example.com",
                    "password456",
                    null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve falhar com dados inválidos - email inválido")
        void shouldFailWithInvalidEmail() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "Test User",
                    "invalid-email",
                    "password123",
                    null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve falhar com senha muito curta")
        void shouldFailWithShortPassword() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "Test User",
                    "test@example.com",
                    "123",
                    null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve falhar com campos obrigatórios vazios")
        void shouldFailWithEmptyRequiredFields() throws Exception {
            RegisterRequest request = new RegisterRequest("", "", "", null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class Login {

        @Test
        @DisplayName("Deve fazer login com sucesso")
        void shouldLoginSuccessfully() throws Exception {
            RegisterRequest registerRequest = new RegisterRequest(
                    "Test User",
                    "login@example.com",
                    "password123",
                    null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            LoginRequest loginRequest = new LoginRequest("login@example.com", "password123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.user.email").value("login@example.com"));
        }

        @Test
        @DisplayName("Deve falhar login com senha incorreta")
        void shouldFailLoginWithWrongPassword() throws Exception {
            RegisterRequest registerRequest = new RegisterRequest(
                    "Test User",
                    "wrong@example.com",
                    "password123",
                    null);

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrongpassword");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve falhar login com email inexistente")
        void shouldFailLoginWithNonExistentEmail() throws Exception {
            LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve falhar login com dados inválidos")
        void shouldFailLoginWithInvalidData() throws Exception {
            LoginRequest loginRequest = new LoginRequest("", "");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}
