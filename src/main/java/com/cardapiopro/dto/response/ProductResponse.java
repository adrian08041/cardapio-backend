package com.cardapiopro.dto.response;

import com.cardapiopro.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal promotionalPrice,
        String imageUrl,
        Integer preparationTime,
        Integer serves,
        boolean available,
        boolean active,
        Integer displayOrder,
        CategorySummary category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record CategorySummary(
            UUID id,
            String name,
            String slug,
            String icon
    ) {}

    public static ProductResponse fromEntity(Product product) {
        CategorySummary categorySummary = null;
        if (product.getCategory() != null) {
            categorySummary = new CategorySummary(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getSlug(),
                    product.getCategory().getIcon()
            );
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getPrice(),
                product.getPromotionalPrice(),
                product.getImageUrl(),
                product.getPreparationTime(),
                product.getServes(),
                product.isAvailable(),
                product.isActive(),
                product.getDisplayOrder(),
                categorySummary,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
