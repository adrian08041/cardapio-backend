package com.cardapiopro.entity;

import com.cardapiopro.entity.enums.PixKeyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StoreSettings Entity Tests")
class StoreSettingsTest {

    @Nested
    @DisplayName("calculateDeliveryFee")
    class CalculateDeliveryFee {

        @Test
        @DisplayName("Deve retornar taxa de entrega normal")
        void shouldReturnNormalDeliveryFee() {
            StoreSettings settings = StoreSettings.builder()
                    .deliveryFee(new BigDecimal("8.00"))
                    .freeDeliveryThreshold(new BigDecimal("100.00"))
                    .build();

            BigDecimal fee = settings.calculateDeliveryFee(new BigDecimal("50.00"));

            assertThat(fee).isEqualByComparingTo(new BigDecimal("8.00"));
        }

        @Test
        @DisplayName("Deve retornar zero acima do limite de frete grátis")
        void shouldReturnZeroAboveThreshold() {
            StoreSettings settings = StoreSettings.builder()
                    .deliveryFee(new BigDecimal("8.00"))
                    .freeDeliveryThreshold(new BigDecimal("100.00"))
                    .build();

            BigDecimal fee = settings.calculateDeliveryFee(new BigDecimal("150.00"));

            assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Deve retornar zero exatamente no limite de frete grátis")
        void shouldReturnZeroAtExactThreshold() {
            StoreSettings settings = StoreSettings.builder()
                    .deliveryFee(new BigDecimal("8.00"))
                    .freeDeliveryThreshold(new BigDecimal("100.00"))
                    .build();

            BigDecimal fee = settings.calculateDeliveryFee(new BigDecimal("100.00"));

            assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Deve retornar taxa quando limite de frete grátis é nulo")
        void shouldReturnFeeWhenThresholdIsNull() {
            StoreSettings settings = StoreSettings.builder()
                    .deliveryFee(new BigDecimal("8.00"))
                    .freeDeliveryThreshold(null)
                    .build();

            BigDecimal fee = settings.calculateDeliveryFee(new BigDecimal("500.00"));

            assertThat(fee).isEqualByComparingTo(new BigDecimal("8.00"));
        }
    }

    @Nested
    @DisplayName("calculatePixDiscount")
    class CalculatePixDiscount {

        @Test
        @DisplayName("Deve calcular desconto PIX corretamente")
        void shouldCalculatePixDiscountCorrectly() {
            StoreSettings settings = StoreSettings.builder()
                    .pixDiscountPercent(new BigDecimal("5.00"))
                    .build();

            BigDecimal discount = settings.calculatePixDiscount(new BigDecimal("100.00"));

            assertThat(discount).isEqualByComparingTo(new BigDecimal("5.00"));
        }

        @Test
        @DisplayName("Deve calcular desconto PIX para valores diferentes")
        void shouldCalculatePixDiscountForDifferentValues() {
            StoreSettings settings = StoreSettings.builder()
                    .pixDiscountPercent(new BigDecimal("10.00"))
                    .build();

            BigDecimal discount = settings.calculatePixDiscount(new BigDecimal("250.00"));

            assertThat(discount).isEqualByComparingTo(new BigDecimal("25.00"));
        }

        @Test
        @DisplayName("Deve retornar zero se desconto PIX é nulo")
        void shouldReturnZeroWhenPixDiscountIsNull() {
            StoreSettings settings = StoreSettings.builder()
                    .pixDiscountPercent(null)
                    .build();

            BigDecimal discount = settings.calculatePixDiscount(new BigDecimal("100.00"));

            assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Deve retornar zero se desconto PIX é zero")
        void shouldReturnZeroWhenPixDiscountIsZero() {
            StoreSettings settings = StoreSettings.builder()
                    .pixDiscountPercent(BigDecimal.ZERO)
                    .build();

            BigDecimal discount = settings.calculatePixDiscount(new BigDecimal("100.00"));

            assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("isCurrentlyOpen")
    class IsCurrentlyOpen {

        @Test
        @DisplayName("Deve retornar true quando loja está aberta")
        void shouldReturnTrueWhenStoreIsOpen() {
            StoreSettings settings = StoreSettings.builder()
                    .isOpen(true)
                    .build();

            assertThat(settings.isCurrentlyOpen()).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando loja está fechada")
        void shouldReturnFalseWhenStoreIsClosed() {
            StoreSettings settings = StoreSettings.builder()
                    .isOpen(false)
                    .build();

            assertThat(settings.isCurrentlyOpen()).isFalse();
        }
    }

    @Nested
    @DisplayName("Builder defaults")
    class BuilderDefaults {

        @Test
        @DisplayName("Deve usar valores padrão do builder")
        void shouldUseDefaultValuesFromBuilder() {
            StoreSettings settings = StoreSettings.builder()
                    .storeName("Test Store")
                    .build();

            assertThat(settings.getIsOpen()).isTrue();
            assertThat(settings.getDeliveryEnabled()).isTrue();
            assertThat(settings.getPickupEnabled()).isTrue();
            assertThat(settings.getDeliveryFee()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(settings.getMinOrderValue()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(settings.getDeliveryTimeMin()).isEqualTo(30);
            assertThat(settings.getDeliveryTimeMax()).isEqualTo(50);
        }

        @Test
        @DisplayName("Deve permitir sobrescrever valores padrão")
        void shouldAllowOverridingDefaultValues() {
            StoreSettings settings = StoreSettings.builder()
                    .storeName("Test Store")
                    .isOpen(false)
                    .deliveryEnabled(false)
                    .deliveryFee(new BigDecimal("10.00"))
                    .deliveryTimeMin(20)
                    .deliveryTimeMax(40)
                    .build();

            assertThat(settings.getIsOpen()).isFalse();
            assertThat(settings.getDeliveryEnabled()).isFalse();
            assertThat(settings.getDeliveryFee()).isEqualByComparingTo(new BigDecimal("10.00"));
            assertThat(settings.getDeliveryTimeMin()).isEqualTo(20);
            assertThat(settings.getDeliveryTimeMax()).isEqualTo(40);
        }
    }
}
