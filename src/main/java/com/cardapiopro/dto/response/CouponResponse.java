package com.cardapiopro.dto.response;

import com.cardapiopro.entity.Coupon;
import com.cardapiopro.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        String description,
        DiscountType type,
        BigDecimal value,
        BigDecimal minOrderValue,
        BigDecimal maxDiscountValue,
        Integer usageLimit,
        Integer maxUsesPerUser,
        Integer usageCount,
        LocalDateTime expirationDate,
        boolean isActive) {
    public static CouponResponse fromEntity(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getType(),
                coupon.getValue(),
                coupon.getMinOrderValue(),
                coupon.getMaxDiscountValue(),
                coupon.getUsageLimit(),
                coupon.getMaxUsesPerUser(),
                coupon.getUsageCount(),
                coupon.getExpirationDate(),
                coupon.isActive());
    }
}