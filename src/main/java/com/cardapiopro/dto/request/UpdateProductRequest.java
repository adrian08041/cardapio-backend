package com.cardapiopro.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(
        @Size(min = 2, max = 150)
        String name,

        @Size(min = 2, max = 150)
        String slug,

        @Size(max = 500)
        String description,

        @DecimalMin(value = "0.01")
        BigDecimal price,

        @DecimalMin(value = "0.01")
        BigDecimal promotionalPrice,

        @Size(max = 500)
        String imageUrl,

        @Min(1)
        Integer preparationTime,

        @Min(1)
        Integer serves,

        Integer displayOrder,

        Boolean available
) {}
