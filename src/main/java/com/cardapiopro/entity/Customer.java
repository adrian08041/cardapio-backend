package com.cardapiopro.entity;

import com.cardapiopro.entity.enums.CustomerStatus;
import com.cardapiopro.entity.enums.LoyaltyTier;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false)
    private String password;

    // Métricas automáticas
    @Column(name = "total_orders", nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "average_ticket", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal averageTicket = BigDecimal.ZERO;

    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;

    @Column(name = "first_order_date")
    private LocalDateTime firstOrderDate;

    @Column(name = "loyalty_points", nullable = false)
    @Builder.Default
    private Integer loyaltyPoints = 0;

    @Column(name = "lifetime_points", nullable = false)
    @Builder.Default
    private Integer lifetimePoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_tier", nullable = false)
    @Builder.Default
    private LoyaltyTier loyaltyTier = LoyaltyTier.BRONZE;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void recalculateTier() {
        if (lifetimePoints >= 10000) {
            this.loyaltyTier = LoyaltyTier.PLATINUM;
        } else if (lifetimePoints >= 5000) {
            this.loyaltyTier = LoyaltyTier.GOLD;
        } else if (lifetimePoints >= 1000) {
            this.loyaltyTier = LoyaltyTier.SILVER;
        } else {
            this.loyaltyTier = LoyaltyTier.BRONZE;
        }
    }

    public void updateMetricsAfterOrder(BigDecimal orderTotal) {
        this.totalOrders++;
        this.totalSpent = this.totalSpent.add(orderTotal);
        this.averageTicket = this.totalSpent.divide(BigDecimal.valueOf(this.totalOrders), 2,
                java.math.RoundingMode.HALF_UP);
        this.lastOrderDate = LocalDateTime.now();
        if (this.firstOrderDate == null) {
            this.firstOrderDate = LocalDateTime.now();
        }
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        this.lifetimePoints += points;
        recalculateTier();
    }

    public boolean redeemPoints(int points) {
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
            return true;
        }
        return false;
    }
}
