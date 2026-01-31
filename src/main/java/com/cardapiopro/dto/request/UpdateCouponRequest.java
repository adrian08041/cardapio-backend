package com.cardapiopro.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateCouponRequest(
        String description,
        BigDecimal value,
        BigDecimal minOrderValue,
        BigDecimal maxDiscountValue,
        Integer usageLimit,
        Integer maxUsesPerUser,
        LocalDateTime expirationDate,
        Boolean active) {
}
