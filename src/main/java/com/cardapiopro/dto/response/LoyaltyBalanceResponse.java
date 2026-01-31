package com.cardapiopro.dto.response;

import com.cardapiopro.entity.Customer;
import com.cardapiopro.entity.enums.LoyaltyTier;

import java.math.BigDecimal;
import java.util.UUID;

public record LoyaltyBalanceResponse(
        UUID customerId,
        String customerName,
        Integer currentPoints,
        Integer lifetimePoints,
        LoyaltyTier tier,
        BigDecimal totalSpent,
        Integer totalOrders,
        Integer pointsToNextTier,
        String nextTier) {
    public static LoyaltyBalanceResponse fromCustomer(Customer customer) {
        int pointsToNext = 0;
        String next = null;

        switch (customer.getLoyaltyTier()) {
            case BRONZE -> {
                pointsToNext = 1000 - customer.getLifetimePoints();
                next = "SILVER";
            }
            case SILVER -> {
                pointsToNext = 5000 - customer.getLifetimePoints();
                next = "GOLD";
            }
            case GOLD -> {
                pointsToNext = 10000 - customer.getLifetimePoints();
                next = "PLATINUM";
            }
            case PLATINUM -> {
                pointsToNext = 0;
                next = null;
            }
        }

        return new LoyaltyBalanceResponse(
                customer.getId(),
                customer.getName(),
                customer.getLoyaltyPoints(),
                customer.getLifetimePoints(),
                customer.getLoyaltyTier(),
                customer.getTotalSpent(),
                customer.getTotalOrders(),
                Math.max(pointsToNext, 0),
                next);
    }
}
