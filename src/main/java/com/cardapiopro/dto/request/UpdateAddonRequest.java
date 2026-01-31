package com.cardapiopro.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateAddonRequest(
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 255)
        String description,

        @DecimalMin("0.00")
        BigDecimal price,

        @Min(1)
        Integer maxQuantity,

        Integer displayOrder,

        Boolean available
) {}