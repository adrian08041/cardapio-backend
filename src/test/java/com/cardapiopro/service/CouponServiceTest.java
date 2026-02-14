package com.cardapiopro.service;

import com.cardapiopro.entity.Coupon;
import com.cardapiopro.entity.enums.DiscountType;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CouponRepository;
import com.cardapiopro.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
                .type(DiscountType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .minOrderValue(new BigDecimal("50"))
                .maxDiscountValue(new BigDecimal("20"))
                .usageLimit(100)
                .usageCount(0)
                .active(true)
                .startDate(LocalDateTime.now().minusDays(1))
                .expirationDate(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Nested
    @DisplayName("findByCode")
    class FindByCode {

        @Test
        @DisplayName("Deve encontrar cupom por código")
        void shouldFindCouponByCode() {
            when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("PROMO10"))
                    .thenReturn(Optional.of(testCoupon));

            Coupon result = couponService.findByCode("PROMO10");

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("PROMO10");
            verify(couponRepository).findByCodeIgnoreCaseAndActiveTrue("PROMO10");
        }

        @Test
        @DisplayName("Deve lançar exceção quando cupom não encontrado")
        void shouldThrowExceptionWhenCouponNotFound() {
            when(couponRepository.findByCodeIgnoreCaseAndActiveTrue(anyString()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.findByCode("INVALID"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("INVALID");
        }
    }

    @Nested
    @DisplayName("calculateDiscount")
    class CalculateDiscount {

        @Test
        @DisplayName("Deve calcular desconto percentual corretamente")
        void shouldCalculatePercentageDiscount() {
            BigDecimal orderTotal = new BigDecimal("100");

            when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("PROMO10"))
                    .thenReturn(Optional.of(testCoupon));

            BigDecimal discount = couponService.calculateDiscount("PROMO10", orderTotal);

            assertThat(discount).isEqualByComparingTo(new BigDecimal("10.00"));
        }

        @Test
        @DisplayName("Deve respeitar limite máximo de desconto")
        void shouldRespectMaxDiscountLimit() {
            BigDecimal orderTotal = new BigDecimal("300"); // 10% = 30, mas máximo é 20

            when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("PROMO10"))
                    .thenReturn(Optional.of(testCoupon));

            BigDecimal discount = couponService.calculateDiscount("PROMO10", orderTotal);

            assertThat(discount).isEqualByComparingTo(new BigDecimal("20.00"));
        }

        @Test
        @DisplayName("Deve calcular desconto fixo corretamente")
        void shouldCalculateFixedDiscount() {
            Coupon fixedCoupon = Coupon.builder()
                    .id(UUID.randomUUID())
                    .code("SAVE15")
                    .type(DiscountType.FIXED)
                    .value(new BigDecimal("15"))
                    .active(true)
                    .startDate(LocalDateTime.now().minusDays(1))
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .build();

            BigDecimal orderTotal = new BigDecimal("100");

            when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("SAVE15"))
                    .thenReturn(Optional.of(fixedCoupon));

            BigDecimal discount = couponService.calculateDiscount("SAVE15", orderTotal);

            assertThat(discount).isEqualByComparingTo(new BigDecimal("15.00"));
        }

        @Test
        @DisplayName("Não deve exceder o valor do pedido")
        void shouldNotExceedOrderTotal() {
            Coupon fixedCoupon = Coupon.builder()
                    .id(UUID.randomUUID())
                    .code("BIG50")
                    .type(DiscountType.FIXED)
                    .value(new BigDecimal("50"))
                    .active(true)
                    .startDate(LocalDateTime.now().minusDays(1))
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .build();

            BigDecimal orderTotal = new BigDecimal("30"); // desconto > total

            when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("BIG50"))
                    .thenReturn(Optional.of(fixedCoupon));

            BigDecimal discount = couponService.calculateDiscount("BIG50", orderTotal);

            assertThat(discount).isEqualByComparingTo(new BigDecimal("30.00"));
        }
    }

    @Nested
    @DisplayName("incrementUsage")
    class IncrementUsage {

        @Test
        @DisplayName("Deve incrementar uso do cupom")
        void shouldIncrementCouponUsage() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            couponService.incrementUsage(couponId);

            assertThat(testCoupon.getUsageCount()).isEqualTo(1);
            verify(couponRepository).save(testCoupon);
        }

        @Test
        @DisplayName("Deve lançar exceção quando limite de uso atingido")
        void shouldThrowExceptionWhenUsageLimitReached() {
            testCoupon.setUsageCount(100); // igual ao usageLimit
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));

            assertThatThrownBy(() -> couponService.incrementUsage(couponId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Limite de uso do cupom atingido");

            verify(couponRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando cupom não encontrado")
        void shouldThrowExceptionWhenCouponNotFound() {
            UUID randomId = UUID.randomUUID();
            when(couponRepository.findById(randomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.incrementUsage(randomId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Deve criar cupom com sucesso")
        void shouldCreateCouponSuccessfully() {
            Coupon newCoupon = Coupon.builder()
                    .code("NEWCOUPON")
                    .type(DiscountType.PERCENTAGE)
                    .value(new BigDecimal("15"))
                    .minOrderValue(new BigDecimal("30"))
                    .active(true)
                    .build();

            when(couponRepository.existsByCodeIgnoreCase("NEWCOUPON")).thenReturn(false);
            when(couponRepository.save(any(Coupon.class))).thenReturn(newCoupon);

            Coupon result = couponService.create(newCoupon);

            assertThat(result).isNotNull();
            verify(couponRepository).save(newCoupon);
        }

        @Test
        @DisplayName("Deve lançar exceção para código duplicado")
        void shouldThrowExceptionForDuplicateCode() {
            Coupon newCoupon = Coupon.builder()
                    .code("PROMO10")
                    .type(DiscountType.PERCENTAGE)
                    .value(new BigDecimal("10"))
                    .build();

            when(couponRepository.existsByCodeIgnoreCase("PROMO10")).thenReturn(true);

            assertThatThrownBy(() -> couponService.create(newCoupon))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Já existe um cupom com este código");

            verify(couponRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve desativar cupom (soft delete)")
        void shouldDeactivateCoupon() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            couponService.delete(couponId);

            assertThat(testCoupon.isActive()).isFalse();
            verify(couponRepository).save(testCoupon);
        }
    }
}
