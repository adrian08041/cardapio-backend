package com.cardapiopro.dto.request;

import com.cardapiopro.entity.Addon;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateAddonRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 255)
        String description,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.00", message = "Preço deve ser >= 0")
        BigDecimal price,

        @Min(value = 1, message = "Quantidade máxima deve ser >= 1")
        Integer maxQuantity,

        Integer displayOrder,

        @NotNull(message = "Categoria do adicional é obrigatória")
        UUID addonCategoryId
) {
    public Addon toEntity() {
        return Addon.builder()
                .name(name)
                .description(description)
                .price(price)
                .maxQuantity(maxQuantity != null ? maxQuantity : 10)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .available(true)
                .active(true)
                .build();
    }
}