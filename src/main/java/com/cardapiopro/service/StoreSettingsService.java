package com.cardapiopro.service;

import com.cardapiopro.dto.request.UpdateStoreSettingsRequest;
import com.cardapiopro.entity.StoreSettings;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.StoreSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreSettingsService {

    private final StoreSettingsRepository repository;

    public StoreSettings getSettings() {
        return repository.findFirstByOrderByUpdatedAtDesc()
                .orElseGet(this::createDefaultSettings);
    }

    @Transactional
    public StoreSettings updateSettings(UpdateStoreSettingsRequest request) {
        StoreSettings settings = getSettings();

        if (request.storeName() != null)
            settings.setStoreName(request.storeName());
        if (request.description() != null)
            settings.setDescription(request.description());
        if (request.whatsapp() != null)
            settings.setWhatsapp(request.whatsapp());
        if (request.address() != null)
            settings.setAddress(request.address());
        if (request.logoUrl() != null)
            settings.setLogoUrl(request.logoUrl());
        if (request.isOpen() != null)
            settings.setIsOpen(request.isOpen());
        if (request.deliveryEnabled() != null)
            settings.setDeliveryEnabled(request.deliveryEnabled());
        if (request.pickupEnabled() != null)
            settings.setPickupEnabled(request.pickupEnabled());
        if (request.deliveryFee() != null)
            settings.setDeliveryFee(request.deliveryFee());
        if (request.minOrderValue() != null)
            settings.setMinOrderValue(request.minOrderValue());
        if (request.deliveryTimeMin() != null)
            settings.setDeliveryTimeMin(request.deliveryTimeMin());
        if (request.deliveryTimeMax() != null)
            settings.setDeliveryTimeMax(request.deliveryTimeMax());
        if (request.freeDeliveryThreshold() != null)
            settings.setFreeDeliveryThreshold(request.freeDeliveryThreshold());
        if (request.pixKey() != null)
            settings.setPixKey(request.pixKey());
        if (request.pixKeyType() != null)
            settings.setPixKeyType(request.pixKeyType());
        if (request.pixDiscountPercent() != null)
            settings.setPixDiscountPercent(request.pixDiscountPercent());
        if (request.businessHours() != null)
            settings.setBusinessHours(request.businessHours());

        return repository.save(settings);
    }

    @Transactional
    public StoreSettings toggleOpen(boolean isOpen) {
        StoreSettings settings = getSettings();
        settings.setIsOpen(isOpen);
        return repository.save(settings);
    }

    @Transactional
    public StoreSettings toggleDelivery(boolean enabled) {
        StoreSettings settings = getSettings();
        settings.setDeliveryEnabled(enabled);
        return repository.save(settings);
    }

    @Transactional
    public StoreSettings togglePickup(boolean enabled) {
        StoreSettings settings = getSettings();
        settings.setPickupEnabled(enabled);
        return repository.save(settings);
    }

    public BigDecimal calculateDeliveryFee(BigDecimal orderTotal) {
        StoreSettings settings = getSettings();
        return settings.calculateDeliveryFee(orderTotal);
    }

    public BigDecimal calculatePixDiscount(BigDecimal orderTotal) {
        StoreSettings settings = getSettings();
        return settings.calculatePixDiscount(orderTotal);
    }

    @Transactional
    private StoreSettings createDefaultSettings() {
        StoreSettings defaultSettings = StoreSettings.builder()
                .storeName("Minha Loja")
                .isOpen(true)
                .deliveryEnabled(true)
                .pickupEnabled(true)
                .deliveryFee(BigDecimal.ZERO)
                .minOrderValue(BigDecimal.ZERO)
                .deliveryTimeMin(30)
                .deliveryTimeMax(50)
                .pixDiscountPercent(new BigDecimal("5.00"))
                .build();
        return repository.save(defaultSettings);
    }
}
