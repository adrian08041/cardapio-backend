-- V8__create_coupons.sql
-- Create coupons table

CREATE TABLE coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    min_order_value DECIMAL(10,2),
    max_discount DECIMAL(10,2),
    max_uses INTEGER,
    max_uses_per_user INTEGER,
    used_count INTEGER NOT NULL DEFAULT 0,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_active ON coupons(active);
CREATE INDEX idx_coupons_dates ON coupons(start_date, end_date);
