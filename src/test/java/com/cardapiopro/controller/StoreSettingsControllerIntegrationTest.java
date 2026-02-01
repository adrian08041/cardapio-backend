package com.cardapiopro.controller;

import com.cardapiopro.entity.StoreSettings;
import com.cardapiopro.entity.enums.PixKeyType;
import com.cardapiopro.repository.StoreSettingsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Map;

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

    @BeforeEach
    void setUp() {
        settingsRepository.deleteAll();
        settingsRepository.save(StoreSettings.builder()
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

    @Test
    @DisplayName("Should get settings (public)")
    void getSettings_Success() throws Exception {
        mockMvc.perform(get("/api/v1/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("Test Store"))
                .andExpect(jsonPath("$.isOpen").value(true))
                .andExpect(jsonPath("$.deliveryFee").value(5.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update settings (admin)")
    void updateSettings_Success() throws Exception {
        Map<String, Object> request = Map.of(
                "storeName", "Updated Store",
                "deliveryFee", 8.00);

        mockMvc.perform(put("/api/v1/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("Updated Store"))
                .andExpect(jsonPath("$.deliveryFee").value(8.00));
    }

    @Test
    @DisplayName("Should deny update without auth")
    void updateSettings_Unauthorized() throws Exception {
        Map<String, Object> request = Map.of("storeName", "Hacked Store");

        mockMvc.perform(put("/api/v1/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should toggle open status")
    void toggleOpen_Success() throws Exception {
        mockMvc.perform(patch("/api/v1/settings/toggle-open")
                .param("open", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOpen").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should toggle delivery status")
    void toggleDelivery_Success() throws Exception {
        mockMvc.perform(patch("/api/v1/settings/toggle-delivery")
                .param("enabled", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryEnabled").value(false));
    }

    @Test
    @DisplayName("Should calculate delivery fee (public)")
    void calculateDeliveryFee_Success() throws Exception {
        mockMvc.perform(get("/api/v1/settings/delivery-fee")
                .param("orderTotal", "50.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("5.00"));
    }

    @Test
    @DisplayName("Should return zero delivery fee above threshold")
    void calculateDeliveryFee_FreeDelivery() throws Exception {
        mockMvc.perform(get("/api/v1/settings/delivery-fee")
                .param("orderTotal", "150.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Should calculate PIX discount (public)")
    void calculatePixDiscount_Success() throws Exception {
        mockMvc.perform(get("/api/v1/settings/pix-discount")
                .param("orderTotal", "100.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("5.00")); // 5% of 100
    }
}
