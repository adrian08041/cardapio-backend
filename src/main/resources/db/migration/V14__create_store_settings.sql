-- V14__create_store_settings.sql
-- Create store settings table

CREATE TABLE store_settings (
    id UUID PRIMARY KEY,
    store_name VARCHAR(200) NOT NULL,
    description TEXT,
    whatsapp VARCHAR(20),
    address VARCHAR(500),
    logo_url VARCHAR(500),
    is_open BOOLEAN NOT NULL DEFAULT true,
    delivery_enabled BOOLEAN NOT NULL DEFAULT true,
    pickup_enabled BOOLEAN NOT NULL DEFAULT true,
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    min_order_value DECIMAL(10,2) NOT NULL DEFAULT 0,
    delivery_time_min INTEGER NOT NULL DEFAULT 30,
    delivery_time_max INTEGER NOT NULL DEFAULT 50,
    free_delivery_threshold DECIMAL(10,2),
    pix_key VARCHAR(100),
    pix_key_type VARCHAR(20),
    pix_discount_percent DECIMAL(4,2) DEFAULT 5.00,
    business_hours JSONB,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default settings
INSERT INTO store_settings (id, store_name, is_open, delivery_enabled, pickup_enabled)
VALUES (gen_random_uuid(), 'Minha Loja', true, true, true);
