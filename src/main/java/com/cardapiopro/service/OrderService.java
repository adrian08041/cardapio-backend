package com.cardapiopro.service;

import com.cardapiopro.entity.*;
import com.cardapiopro.entity.enums.OrderStatus;
import com.cardapiopro.entity.enums.PaymentStatus;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final AddonService addonService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));
    }

    public Order findByOrderNumber(Integer orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: #" + orderNumber));
    }

    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<Order> findActiveOrders() {
        return orderRepository.findActiveOrders();
    }

    public List<Order> findTodayOrders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return orderRepository.findTodayOrders(startOfDay);
    }

    public List<Order> findByCustomerPhone(String phone) {
        return orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone);
    }

    @Transactional
    public Order create(Order order, List<OrderItemRequest> itemRequests) {
        order.setOrderNumber(orderRepository.getNextOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        for (OrderItemRequest itemReq : itemRequests) {
            OrderItem item = createOrderItem(itemReq);
            order.addItem(item);
        }

        order.calculateTotals();
        int estimatedTime = order.getItems().stream()
                .mapToInt(item -> {
                    Integer prepTime = item.getProduct().getPreparationTime();
                    return prepTime != null ? prepTime : 15;
                })
                .max()
                .orElse(30);
        order.setEstimatedTime(estimatedTime);

        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(OrderItemRequest request) {
        Product product = productService.findById(request.productId());

        OrderItem item = OrderItem.builder()
                .product(product)
                .productName(product.getName())
                .quantity(request.quantity())
                .unitPrice(product.getPromotionalPrice() != null ?
                        product.getPromotionalPrice() : product.getPrice())
                .notes(request.notes())
                .build();

        if (request.addons() != null) {
            for (OrderAddonRequest addonReq : request.addons()) {
                Addon addon = addonService.findById(addonReq.addonId());

                OrderItemAddon itemAddon = OrderItemAddon.builder()
                        .addon(addon)
                        .addonName(addon.getName())
                        .quantity(addonReq.quantity())
                        .price(addon.getPrice())
                        .build();

                item.addAddon(itemAddon);
            }
        }

        item.calculateSubtotal();
        return item;
    }

    @Transactional
    public Order updateStatus(UUID id, OrderStatus newStatus) {
        Order order = findById(id);

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (next == OrderStatus.PENDING && current != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Não é possível voltar para PENDING");
        }

        if (current == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Pedido cancelado não pode ser alterado");
        }

        if (current == OrderStatus.DELIVERED && next != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Pedido entregue não pode ser alterado");
        }
    }

    @Transactional
    public Order cancel(UUID id, String reason) {
        Order order = findById(id);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Pedido já entregue não pode ser cancelado");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setNotes(order.getNotes() != null ?
                order.getNotes() + " | Cancelado: " + reason :
                "Cancelado: " + reason);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updatePaymentStatus(UUID id, PaymentStatus paymentStatus) {
        Order order = findById(id);
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    public record OrderItemRequest(
            UUID productId,
            Integer quantity,
            String notes,
            List<OrderAddonRequest> addons
    ) {}

    public record OrderAddonRequest(
            UUID addonId,
            Integer quantity
    ) {}
}