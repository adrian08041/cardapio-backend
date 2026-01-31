package com.cardapiopro.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateAddonCategoryRequest(
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 255)
        String description,

        @Min(0)
        Integer minSelection,

        @Min(1)
        Integer maxSelection,

        Boolean required,

        Integer displayOrder
) {}