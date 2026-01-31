package com.cardapiopro.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RedeemPointsRequest(
        @NotNull(message = "Quantidade de pontos é obrigatória") @Min(value = 1, message = "Mínimo de 1 ponto para resgate") Integer points,

        String description) {
}
