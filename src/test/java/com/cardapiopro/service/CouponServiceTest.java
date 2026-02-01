package com.cardapiopro.service;

import com.cardapiopro.entity.Coupon;
import com.cardapiopro.entity.enums.CouponType;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CouponRepository;
import com.cardapiopro.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService Tests")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon testCoupon;
    private UUID couponId;

    @BeforeEach
    void setUp() {
        couponId = UUID.randomUUID();
        testCoupon = Coupon.builder()
                .id(couponId)
                .code("PROMO10")
                .type(CouponType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .minOrderValue(new BigDecimal("50"))
                .maxDiscount(new BigDecimal("20"))
                .maxUses(100)
                .usedCount(0)
                .active(true)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    @DisplayName("Should find coupon by code")
    void findByCode_Success() {
        // Arrange
        when(couponRepository.findByCodeIgnoreCase(anyString())).thenReturn(Optional.of(testCoupon));

        // Act
        Coupon result = couponService.findByCode("PROMO10");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("PROMO10");
    }

    @Test
    @DisplayName("Should throw exception when coupon not found")
    void findByCode_NotFound() {
        // Arrange
        when(couponRepository.findByCodeIgnoreCase(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> couponService.findByCode("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should calculate percentage discount correctly")
    void calculateDiscount_Percentage() {
        // Arrange
        BigDecimal orderTotal = new BigDecimal("100");

        when(couponRepository.findByCodeIgnoreCase(anyString())).thenReturn(Optional.of(testCoupon));

        // Act
        BigDecimal discount = couponService.calculateDiscount("PROMO10", orderTotal);

        // Assert
        assertThat(discount).isEqualByComparingTo(new BigDecimal("10.00")); // 10% of 100
    }

    @Test
    @DisplayName("Should respect max discount limit")
    void calculateDiscount_RespectMaxDiscount() {
        // Arrange
        BigDecimal orderTotal = new BigDecimal("300"); // 10% = 30, but max is 20

        when(couponRepository.findByCodeIgnoreCase(anyString())).thenReturn(Optional.of(testCoupon));

        // Act
        BigDecimal discount = couponService.calculateDiscount("PROMO10", orderTotal);

        // Assert
        assertThat(discount).isEqualByComparingTo(new BigDecimal("20.00")); // Limited by maxDiscount
    }

    @Test
    @DisplayName("Should calculate fixed discount correctly")
    void calculateDiscount_Fixed() {
        // Arrange
        Coupon fixedCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("SAVE15")
                .type(CouponType.FIXED)
                .value(new BigDecimal("15"))
                .active(true)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        BigDecimal orderTotal = new BigDecimal("100");

        when(couponRepository.findByCodeIgnoreCase(anyString())).thenReturn(Optional.of(fixedCoupon));

        // Act
        BigDecimal discount = couponService.calculateDiscount("SAVE15", orderTotal);

        // Assert
        assertThat(discount).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    @DisplayName("Should increment used count")
    void incrementUsage_Success() {
        // Arrange
        when(couponRepository.findByCodeIgnoreCase(anyString())).thenReturn(Optional.of(testCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        // Act
        couponService.incrementUsage("PROMO10");

        // Assert
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Should create coupon successfully")
    void create_Success() {
        // Arrange
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        // Act
        Coupon result = couponService.create(
                "PROMO10",
                CouponType.PERCENTAGE,
                new BigDecimal("10"),
                new BigDecimal("50"),
                new BigDecimal("20"),
                100,
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(30));

        // Assert
        assertThat(result).isNotNull();
        verify(couponRepository).save(any(Coupon.class));
    }
}
