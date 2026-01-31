package com.cardapiopro.dto.request;

import com.cardapiopro.entity.AddonCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public record CreateAddonCategoryRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String name,

        @Size(max = 255)
        String description,

        @Min(value = 0, message = "Seleção mínima deve ser >= 0")
        Integer minSelection,

        @Min(value = 1, message = "Seleção máxima deve ser >= 1")
        Integer maxSelection,

        Boolean required,

        Integer displayOrder
) {
    public AddonCategory toEntity() {
        return AddonCategory.builder()
                .name(name)
                .description(description)
                .minSelection(minSelection != null ? minSelection : 0)
                .maxSelection(maxSelection != null ? maxSelection : 1)
                .required(required != null ? required : false)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .active(true)
                .build();
    }
}
