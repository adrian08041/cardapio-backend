package com.cardapiopro.dto.response;

import com.cardapiopro.entity.Category;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String icon,
        Integer displayOrder,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getIcon(),
                category.getDisplayOrder(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
