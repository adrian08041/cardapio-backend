package com.cardapiopro.dto.request;

import com.cardapiopro.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status é obrigatório")
        OrderStatus status
) {}