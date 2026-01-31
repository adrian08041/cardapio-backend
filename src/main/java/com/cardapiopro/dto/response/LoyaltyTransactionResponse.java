package com.cardapiopro.dto.response;

import com.cardapiopro.entity.LoyaltyTransaction;
import com.cardapiopro.entity.enums.LoyaltyTransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoyaltyTransactionResponse(
        UUID id,
        LoyaltyTransactionType type,
        Integer points,
        String description,
        UUID orderId,
        LocalDateTime createdAt) {
    public static LoyaltyTransactionResponse fromEntity(LoyaltyTransaction transaction) {
        return new LoyaltyTransactionResponse(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getPoints(),
                transaction.getDescription(),
                transaction.getOrder() != null ? transaction.getOrder().getId() : null,
                transaction.getCreatedAt());
    }
}
