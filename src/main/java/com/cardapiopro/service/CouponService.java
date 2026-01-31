package com.cardapiopro.service;

import com.cardapiopro.entity.Coupon;
import com.cardapiopro.entity.enums.DiscountType;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CouponRepository;
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