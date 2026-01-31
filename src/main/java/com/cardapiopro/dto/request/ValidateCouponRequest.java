package com.cardapiopro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ValidateCouponRequest(
        @NotBlank(message = "Código é obrigatório") String code,

        UUID customerId,

        @NotNull(message = "Subtotal do pedido é obrigatório") @Positive(message = "Subtotal deve ser maior que zero") BigDecimal orderSubtotal) {
}
