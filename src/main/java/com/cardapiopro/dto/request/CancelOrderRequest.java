package com.cardapiopro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelOrderRequest(
        @NotBlank(message = "Motivo do cancelamento é obrigatório")
        @Size(min = 5, max = 200)
        String reason
) {}