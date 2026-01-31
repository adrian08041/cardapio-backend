package com.cardapiopro.dto.request;

import com.cardapiopro.entity.StoreSettings.BusinessHour;
import com.cardapiopro.entity.enums.PixKeyType;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateStoreSettingsRequest(
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
        Map<String, BusinessHour> businessHours) {
}
