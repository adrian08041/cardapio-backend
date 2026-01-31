package com.cardapiopro.dto.response;

import com.cardapiopro.entity.AddonCategory;
import java.time.LocalDateTime;
import java.util.UUID;

public record AddonCategoryResponse(
        UUID id,
        String name,
        String description,
        Integer minSelection,
        Integer maxSelection,
        boolean required,
        Integer displayOrder,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AddonCategoryResponse fromEntity(AddonCategory category) {
        return new AddonCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getMinSelection(),
                category.getMaxSelection(),
                category.isRequired(),
                category.getDisplayOrder(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
