package com.cardapiopro.dto.response;

import com.cardapiopro.entity.enums.DiscountType;
import java.math.BigDecimal;

public record ValidateCouponResponse(
        boolean valid,
        String code,
        DiscountType type,
        BigDecimal discountAmount,
        String message) {
}
