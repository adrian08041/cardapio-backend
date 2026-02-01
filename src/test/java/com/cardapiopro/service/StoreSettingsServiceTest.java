package com.cardapiopro.service;

import com.cardapiopro.entity.StoreSettings;
import com.cardapiopro.entity.enums.PixKeyType;
import com.cardapiopro.repository.StoreSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreSettingsService Tests")
class StoreSettingsServiceTest {

    @Mock
    private StoreSettingsRepository repository;

    @InjectMocks
    private StoreSettingsService service;

    private StoreSettings testSettings;

    @BeforeEach
    void setUp() {
        testSettings = StoreSettings.builder()
                .id(UUID.randomUUID())
                .storeName("Minha Loja")
                .isOpen(true)
                .deliveryEnabled(true)
                .pickupEnabled(true)
                .deliveryFee(new BigDecimal("5.00"))
                .minOrderValue(new BigDecimal("20.00"))
                .deliveryTimeMin(30)
                .deliveryTimeMax(50)
                .freeDeliveryThreshold(new BigDecimal("100.00"))
                .pixKey("email@example.com")
                .pixKeyType(PixKeyType.EMAIL)
                .pixDiscountPercent(new BigDecimal("5.00"))
                .build();
    }

    @Test
    @DisplayName("Should get existing settings")
    void getSettings_ExistingSettings() {
        // Arrange
        when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

        // Act
        StoreSettings result = service.getSettings();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStoreName()).isEqualTo("Minha Loja");
    }

    @Test
    @DisplayName("Should toggle open status")
    void toggleOpen_Success() {
        // Arrange
        when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
        when(repository.save(any(StoreSettings.class))).thenReturn(testSettings);

        // Act
        StoreSettings result = service.toggleOpen(false);

        // Assert
        verify(repository).save(any(StoreSettings.class));
    }

    @Test
    @DisplayName("Should toggle delivery status")
    void toggleDelivery_Success() {
        // Arrange
        when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
        when(repository.save(any(StoreSettings.class))).thenReturn(testSettings);

        // Act
        StoreSettings result = service.toggleDelivery(false);

        // Assert
        verify(repository).save(any(StoreSettings.class));
    }

    @Test
    @DisplayName("Should calculate delivery fee")
    void calculateDeliveryFee_Normal() {
        // Arrange
        when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

        // Act
        BigDecimal fee = service.calculateDeliveryFee(new BigDecimal("50.00"));

        // Assert
        assertThat(fee).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("Should return zero delivery fee above threshold")
    void calculateDeliveryFee_FreeDelivery() {
        // Arrange
        when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

        // Act
        BigDecimal fee = service.calculateDeliveryFee(new BigDecimal("150.00"));

        // Assert
        assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate PIX discount")
    void calculatePixDiscount_Success() {
        // Arrange
        when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

        // Act
        BigDecimal discount = service.calculatePixDiscount(new BigDecimal("100.00"));

        // Assert
        assertThat(discount).isEqualByComparingTo(new BigDecimal("5.00")); // 5% of 100
    }
}
