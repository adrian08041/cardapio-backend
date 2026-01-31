package com.cardapiopro.service;

import com.cardapiopro.entity.Coupon;
import com.cardapiopro.entity.enums.DiscountType;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.dto.request.UpdateCouponRequest;
import com.cardapiopro.dto.request.ValidateCouponRequest;
import com.cardapiopro.dto.response.ValidateCouponResponse;
import com.cardapiopro.repository.CouponRepository;
import com.cardapiopro.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    public Coupon findById(UUID id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado: " + id));
    }

    public Coupon findByCode(String code) {
        return couponRepository.findByCodeIgnoreCaseAndActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom inválido ou expirado: " + code));
    }

    public BigDecimal calculateDiscount(String code, BigDecimal subtotal) {
        Coupon coupon = findByCode(code);

        if (!coupon.isValid(subtotal)) {
            throw new IllegalArgumentException(
                    "Este cupom não é aplicável a este pedido (verifique valor mínimo ou validade)");
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (coupon.getType() == DiscountType.FIXED) {
            discount = coupon.getValue();
        } else {
            discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaxDiscountValue() != null && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
                discount = coupon.getMaxDiscountValue();
            }
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return discount;
    }

    public ValidateCouponResponse validateCoupon(ValidateCouponRequest request) {
        try {
            Coupon coupon = findByCode(request.code());

            if (!coupon.isValid(request.orderSubtotal())) {
                return new ValidateCouponResponse(false, request.code(), coupon.getType(), BigDecimal.ZERO,
                        "Cupom não aplicável (valor mínimo ou validade)");
            }

            if (coupon.getMaxUsesPerUser() != null && request.customerId() != null) {
                long userUsage = orderRepository.countByCouponIdAndCustomerId(coupon.getId(), request.customerId());
                if (userUsage >= coupon.getMaxUsesPerUser()) {
                    return new ValidateCouponResponse(false, request.code(), coupon.getType(), BigDecimal.ZERO,
                            "Limite de uso por usuário atingido");
                }
            }

            BigDecimal discount = calculateDiscountValue(coupon, request.orderSubtotal());
            return new ValidateCouponResponse(true, coupon.getCode(), coupon.getType(), discount,
                    "Cupom aplicado com sucesso");

        } catch (ResourceNotFoundException e) {
            return new ValidateCouponResponse(false, request.code(), null, BigDecimal.ZERO,
                    "Cupom inválido ou expirado");
        } catch (Exception e) {
            return new ValidateCouponResponse(false, request.code(), null, BigDecimal.ZERO, e.getMessage());
        }
    }

    private BigDecimal calculateDiscountValue(Coupon coupon, BigDecimal subtotal) {
        BigDecimal discount;
        if (coupon.getType() == DiscountType.FIXED) {
            discount = coupon.getValue();
        } else {
            discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountValue() != null && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
                discount = coupon.getMaxDiscountValue();
            }
        }
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        return discount;
    }

    @Transactional
    public Coupon update(UUID id, UpdateCouponRequest request) {
        Coupon coupon = findById(id);

        if (request.description() != null)
            coupon.setDescription(request.description());
        if (request.value() != null)
            coupon.setValue(request.value());
        if (request.minOrderValue() != null)
            coupon.setMinOrderValue(request.minOrderValue());
        if (request.maxDiscountValue() != null)
            coupon.setMaxDiscountValue(request.maxDiscountValue());
        if (request.usageLimit() != null)
            coupon.setUsageLimit(request.usageLimit());
        if (request.maxUsesPerUser() != null)
            coupon.setMaxUsesPerUser(request.maxUsesPerUser());
        if (request.expirationDate() != null)
            coupon.setExpirationDate(request.expirationDate());
        if (request.active() != null)
            coupon.setActive(request.active());

        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon create(Coupon coupon) {
        if (couponRepository.existsByCodeIgnoreCase(coupon.getCode())) {
            throw new IllegalArgumentException("Já existe um cupom com este código: " + coupon.getCode());
        }
        return couponRepository.save(coupon);
    }

    @Transactional
    public void incrementUsage(UUID id) {
        Coupon coupon = findById(id);
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new IllegalStateException("Limite de uso do cupom atingido");
        }
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);
    }

    @Transactional
    public void delete(UUID id) {
        Coupon coupon = findById(id);
        coupon.setActive(false);
        couponRepository.save(coupon);
    }
}