package com.cardapiopro.dto.response;

import com.cardapiopro.entity.StoreSettings;
import com.cardapiopro.entity.StoreSettings.BusinessHour;
import com.cardapiopro.entity.enums.PixKeyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record StoreSettingsResponse(
        UUID id,
        String storeName,
        String description,
        String whatsapp,
        String address,
        String logoUrl,
        Boolean isOpen,
        Boolean deliveryEnabled,
        Boolean pickupEnabled,
        BigDecimal deliveryFee,
        BigDecimal minOrderValue,
        Integer deliveryTimeMin,
        Integer deliveryTimeMax,
        BigDecimal freeDeliveryThreshold,
        String pixKey,
        PixKeyType pixKeyType,
        BigDecimal pixDiscountPercent,
        Map<String, BusinessHour> businessHours,
        LocalDateTime updatedAt) {
    public static StoreSettingsResponse fromEntity(StoreSettings settings) {
        return new StoreSettingsResponse(
                settings.getId(),
                settings.getStoreName(),
                settings.getDescription(),
                settings.getWhatsapp(),
                settings.getAddress(),
                settings.getLogoUrl(),
                settings.getIsOpen(),
                settings.getDeliveryEnabled(),
                settings.getPickupEnabled(),
                settings.getDeliveryFee(),
                settings.getMinOrderValue(),
                settings.getDeliveryTimeMin(),
                settings.getDeliveryTimeMax(),
                settings.getFreeDeliveryThreshold(),
                settings.getPixKey(),
                settings.getPixKeyType(),
                settings.getPixDiscountPercent(),
                settings.getBusinessHours(),
                settings.getUpdatedAt());
    }
}
