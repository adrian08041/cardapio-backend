package com.cardapiopro.entity;

import com.cardapiopro.entity.enums.PixKeyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "store_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String whatsapp;

    @Column(length = 500)
    private String address;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "is_open", nullable = false)
    @Builder.Default
    private Boolean isOpen = true;

    @Column(name = "delivery_enabled", nullable = false)
    @Builder.Default
    private Boolean deliveryEnabled = true;

    @Column(name = "pickup_enabled", nullable = false)
    @Builder.Default
    private Boolean pickupEnabled = true;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "min_order_value", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "delivery_time_min", nullable = false)
    @Builder.Default
    private Integer deliveryTimeMin = 30;

    @Column(name = "delivery_time_max", nullable = false)
    @Builder.Default
    private Integer deliveryTimeMax = 50;

    @Column(name = "free_delivery_threshold", precision = 10, scale = 2)
    private BigDecimal freeDeliveryThreshold;

    @Column(name = "pix_key", length = 100)
    private String pixKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "pix_key_type")
    private PixKeyType pixKeyType;

    @Column(name = "pix_discount_percent", precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal pixDiscountPercent = new BigDecimal("5.00");

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "business_hours", columnDefinition = "jsonb")
    private Map<String, BusinessHour> businessHours;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessHour {
        private String open;
        private String close;
        private boolean closed;
    }

    public boolean isCurrentlyOpen() {
        if (!isOpen)
            return false;
        return true;
    }

    public BigDecimal calculateDeliveryFee(BigDecimal orderTotal) {
        if (freeDeliveryThreshold != null && orderTotal.compareTo(freeDeliveryThreshold) >= 0) {
            return BigDecimal.ZERO;
        }
        return deliveryFee;
    }

    public BigDecimal calculatePixDiscount(BigDecimal orderTotal) {
        if (pixDiscountPercent == null || pixDiscountPercent.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return orderTotal.multiply(pixDiscountPercent).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }
}
