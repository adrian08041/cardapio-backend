package com.cardapiopro.dto.request;

import com.cardapiopro.entity.enums.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest(
                @NotBlank(message = "Código é obrigatório") @Size(min = 3, max = 50) String code,

                String description,

                @NotNull(message = "Tipo de desconto é obrigatório") DiscountType type,

                @NotNull(message = "Valor do desconto é obrigatório") @Positive(message = "Valor deve ser positivo") BigDecimal value,

                @Positive(message = "Valor mínimo deve ser positivo") BigDecimal minOrderValue,

                @Positive(message = "Valor máximo de desconto deve ser positivo") BigDecimal maxDiscountValue,

                @Min(1) Integer usageLimit,

                @Min(1) Integer maxUsesPerUser,

                LocalDateTime startDate,
                LocalDateTime expirationDate) {
}