package com.cardapiopro.controller;

import com.cardapiopro.dto.request.UpdateStoreSettingsRequest;
import com.cardapiopro.entity.StoreSettings;
import com.cardapiopro.entity.enums.PixKeyType;
import com.cardapiopro.repository.StoreSettingsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("StoreSettingsController Integration Tests")
class StoreSettingsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreSettingsRepository settingsRepository;

    private StoreSettings testSettings;

    @BeforeEach
    void setUp() {
        settingsRepository.deleteAll();
        testSettings = settingsRepository.save(StoreSettings.builder()
                .storeName("Test Store")
                .isOpen(true)
                .deliveryEnabled(true)
                .pickupEnabled(true)
                .deliveryFee(new BigDecimal("5.00"))
                .minOrderValue(new BigDecimal("20.00"))
                .deliveryTimeMin(30)
                .deliveryTimeMax(50)
                .freeDeliveryThreshold(new BigDecimal("100.00"))
                .pixKey("pix@test.com")
                .pixKeyType(PixKeyType.EMAIL)
                .pixDiscountPercent(new BigDecimal("5.00"))
                .build());
    }

    @Nested
    @DisplayName("GET /api/v1/settings")
    class GetSettings {

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Deve retornar configurações para usuário autenticado")
        void shouldReturnSettingsForAuthenticatedUser() throws Exception {
            mockMvc.perform(get("/api/v1/settings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.storeName").value("Test Store"))
                    .andExpect(jsonPath("$.isOpen").value(true))
                    .andExpect(jsonPath("$.deliveryEnabled").value(true))
                    .andExpect(jsonPath("$.deliveryFee").value(5.00))
                    .andExpect(jsonPath("$.freeDeliveryThreshold").value(100.00))
                    .andExpect(jsonPath("$.pixDiscountPercent").value(5.00));
        }

        @Test
        @DisplayName("Deve negar acesso sem autenticação")
        void shouldDenyAccessWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/api/v1/settings"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/settings")
    class UpdateSettings {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin deve conseguir atualizar configurações")
        void adminShouldUpdateSettings() throws Exception {
            UpdateStoreSettingsRequest request = new UpdateStoreSettingsRequest(
                    "Updated Store", null, null, null, null, null, null, null,
                    new BigDecimal("8.00"), null, null, null, null, null, null, null, null);

            mockMvc.perform(put("/api/v1/settings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.storeName").value("Updated Store"))
                    .andExpect(jsonPath("$.deliveryFee").value(8.00));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Customer não deve conseguir atualizar configurações")
        void customerShouldNotUpdateSettings() throws Exception {
            UpdateStoreSettingsRequest request = new UpdateStoreSettingsRequest(
                    "Hacked Store", null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);

            mockMvc.perform(put("/api/v1/settings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve negar atualização sem autenticação")
        void shouldDenyUpdateWithoutAuthentication() throws Exception {
            UpdateStoreSettingsRequest request = new UpdateStoreSettingsRequest(
                    "Hacked Store", null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);

            mockMvc.perform(put("/api/v1/settings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/settings/toggle-open")
    class ToggleOpen {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin deve conseguir fechar a loja")
        void adminShouldCloseStore() throws Exception {
            mockMvc.perform(patch("/api/v1/settings/toggle-open")
                            .param("open", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isOpen").value(false));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin deve conseguir abrir a loja")
        void adminShouldOpenStore() throws Exception {
            testSettings.setIsOpen(false);
            settingsRepository.save(testSettings);

            mockMvc.perform(patch("/api/v1/settings/toggle-open")
                            .param("open", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isOpen").value(true));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Customer não deve conseguir alterar status da loja")
        void customerShouldNotToggleOpen() throws Exception {
            mockMvc.perform(patch("/api/v1/settings/toggle-open")
                            .param("open", "false"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/settings/toggle-delivery")
    class ToggleDelivery {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin deve conseguir desabilitar delivery")
        void adminShouldDisableDelivery() throws Exception {
            mockMvc.perform(patch("/api/v1/settings/toggle-delivery")
                            .param("enabled", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deliveryEnabled").value(false));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Customer não deve conseguir alterar delivery")
        void customerShouldNotToggleDelivery() throws Exception {
            mockMvc.perform(patch("/api/v1/settings/toggle-delivery")
                            .param("enabled", "false"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/settings/delivery-fee")
    class CalculateDeliveryFee {

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Deve calcular taxa de entrega normalmente")
        void shouldCalculateDeliveryFee() throws Exception {
            mockMvc.perform(get("/api/v1/settings/delivery-fee")
                            .param("orderTotal", "50.00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5.00"));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Deve retornar zero quando acima do limite para frete grátis")
        void shouldReturnZeroWhenAboveThreshold() throws Exception {
            mockMvc.perform(get("/api/v1/settings/delivery-fee")
                            .param("orderTotal", "150.00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/settings/pix-discount")
    class CalculatePixDiscount {

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Deve calcular desconto PIX corretamente")
        void shouldCalculatePixDiscount() throws Exception {
            mockMvc.perform(get("/api/v1/settings/pix-discount")
                            .param("orderTotal", "100.00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5.00"));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Deve calcular desconto PIX para valor diferente")
        void shouldCalculatePixDiscountForDifferentValue() throws Exception {
            mockMvc.perform(get("/api/v1/settings/pix-discount")
                            .param("orderTotal", "200.00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("10.00"));
        }
    }
}
