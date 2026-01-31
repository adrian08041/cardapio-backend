package com.cardapiopro.repository;

import com.cardapiopro.entity.Order;
import com.cardapiopro.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(Integer orderNumber);
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    List<Order> findByCustomerPhoneOrderByCreatedAtDesc(String phone);
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startOfDay ORDER BY o.createdAt DESC")
    List<Order> findTodayOrders(LocalDateTime startOfDay);
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED', 'PREPARING') ORDER BY o.createdAt ASC")
    List<Order> findActiveOrders();
    @Query("SELECT COALESCE(MAX(o.orderNumber), 0) + 1 FROM Order o")
    Integer getNextOrderNumber();
    long countByStatus(OrderStatus status);
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
}