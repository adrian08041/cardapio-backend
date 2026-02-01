package com.cardapiopro.entity;

import com.cardapiopro.entity.enums.PixKeyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StoreSettings Entity Tests")
class StoreSettingsTest {

    @Test
    @DisplayName("Should calculate delivery fee normally")
    void calculateDeliveryFee_Normal() {
        StoreSettings settings = StoreSettings.builder()
                .deliveryFee(new BigDecimal("8.00"))
                .freeDeliveryThreshold(new BigDecimal("100.00"))
                .build();

        BigDecimal fee = settings.calculateDeliveryFee(new BigDecimal("50.00"));

        assertThat(fee).isEqualByComparingTo(new BigDecimal("8.00"));
    }

    @Test
    @DisplayName("Should return zero for free delivery threshold")
    void calculateDeliveryFee_Free() {
        StoreSettings settings = StoreSettings.builder()
                .deliveryFee(new BigDecimal("8.00"))
                .freeDeliveryThreshold(new BigDecimal("100.00"))
                .build();

        BigDecimal fee = settings.calculateDeliveryFee(new BigDecimal("150.00"));

        assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate PIX discount correctly")
    void calculatePixDiscount_Success() {
        StoreSettings settings = StoreSettings.builder()
                .pixDiscountPercent(new BigDecimal("5.00"))
                .build();

        BigDecimal discount = settings.calculatePixDiscount(new BigDecimal("100.00"));

        assertThat(discount).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("Should return zero if PIX discount is null")
    void calculatePixDiscount_Null() {
        StoreSettings settings = StoreSettings.builder()
                .pixDiscountPercent(null)
                .build();

        BigDecimal discount = settings.calculatePixDiscount(new BigDecimal("100.00"));

        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should check if store is currently open")
    void isCurrentlyOpen_True() {
        StoreSettings settings = StoreSettings.builder()
                .isOpen(true)
                .build();

        assertThat(settings.isCurrentlyOpen()).isTrue();
    }

    @Test
    @DisplayName("Should return false if store is closed")
    void isCurrentlyOpen_False() {
        StoreSettings settings = StoreSettings.builder()
                .isOpen(false)
                .build();

        assertThat(settings.isCurrentlyOpen()).isFalse();
    }

    @Test
    @DisplayName("Should use default values from builder")
    void defaultValues() {
        StoreSettings settings = StoreSettings.builder()
                .storeName("Test Store")
                .build();

        assertThat(settings.getIsOpen()).isTrue();
        assertThat(settings.getDeliveryEnabled()).isTrue();
        assertThat(settings.getPickupEnabled()).isTrue();
        assertThat(settings.getDeliveryFee()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(settings.getDeliveryTimeMin()).isEqualTo(30);
        assertThat(settings.getDeliveryTimeMax()).isEqualTo(50);
    }
}
