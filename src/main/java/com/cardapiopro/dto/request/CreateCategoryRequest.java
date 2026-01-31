package com.cardapiopro.dto.request;

import com.cardapiopro.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") String name,

        @NotBlank(message = "Slug é obrigatório") @Size(min = 2, max = 100, message = "Slug deve ter entre 2 e 100 caracteres") String slug,

        @Size(max = 50, message = "Ícone deve ter no máximo 50 caracteres") String icon,

        Integer displayOrder) {
    public Category toEntity() {
        return Category.builder()
                .name(name)
                .slug(slug)
                .icon(icon)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .active(true)
                .build();
    }
}
