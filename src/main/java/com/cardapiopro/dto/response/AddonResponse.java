package com.cardapiopro.dto.response;
import com.cardapiopro.entity.Addon;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AddonResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer maxQuantity,
        boolean available,
        boolean active,
        Integer displayOrder,
        AddonCategorySummary category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record AddonCategorySummary(
            UUID id,
            String name
    ) {}

    public static AddonResponse fromEntity(Addon addon) {
        AddonCategorySummary categorySummary = null;
        if (addon.getAddonCategory() != null) {
            categorySummary = new AddonCategorySummary(
                    addon.getAddonCategory().getId(),
                    addon.getAddonCategory().getName()
            );
        }

        return new AddonResponse(
                addon.getId(),
                addon.getName(),
                addon.getDescription(),
                addon.getPrice(),
                addon.getMaxQuantity(),
                addon.isAvailable(),
                addon.isActive(),
                addon.getDisplayOrder(),
                categorySummary,
                addon.getCreatedAt(),
                addon.getUpdatedAt()
        );
    }
}