package com.cardapiopro.dto.response;

import com.cardapiopro.entity.Order;
import com.cardapiopro.entity.OrderItem;
import com.cardapiopro.entity.OrderItemAddon;
import com.cardapiopro.entity.enums.OrderStatus;
import com.cardapiopro.entity.enums.OrderType;
import com.cardapiopro.entity.enums.PaymentMethod;
import com.cardapiopro.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        Integer orderNumber,

        String customerName,
        String customerPhone,
        String customerEmail,

        String deliveryAddress,
        String deliveryComplement,
        String deliveryNeighborhood,

        OrderType orderType,
        OrderStatus status,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,

        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal discount,
        BigDecimal total,
        BigDecimal changeFor,


        String notes,
        Integer estimatedTime,

        List<OrderItemResponse> items,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record OrderItemResponse(
            UUID id,
            UUID productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            String notes,
            List<OrderAddonResponse> addons,
            BigDecimal subtotal
    ) {
        public static OrderItemResponse fromEntity(OrderItem item) {
            return new OrderItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getNotes(),
                    item.getAddons().stream()
                            .map(OrderAddonResponse::fromEntity)
                            .toList(),
                    item.getSubtotal()
            );
        }
    }

    public record OrderAddonResponse(
            UUID id,
            UUID addonId,
            String addonName,
            Integer quantity,
            BigDecimal price
    ) {
        public static OrderAddonResponse fromEntity(OrderItemAddon addon) {
            return new OrderAddonResponse(
                    addon.getId(),
                    addon.getAddon().getId(),
                    addon.getAddonName(),
                    addon.getQuantity(),
                    addon.getPrice()
            );
        }
    }

    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getCustomerEmail(),
                order.getDeliveryAddress(),
                order.getDeliveryComplement(),
                order.getDeliveryNeighborhood(),
                order.getOrderType(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getSubtotal(),
                order.getDeliveryFee(),
                order.getDiscount(),
                order.getTotal(),
                order.getChangeFor(),
                order.getNotes(),
                order.getEstimatedTime(),
                order.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .toList(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}