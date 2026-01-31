package com.cardapiopro.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") String name,

        @Size(min = 2, max = 100, message = "Slug deve ter entre 2 e 100 caracteres") String slug,

        @Size(max = 50, message = "Ícone deve ter no máximo 50 caracteres") String icon,

        Integer displayOrder) {
}
