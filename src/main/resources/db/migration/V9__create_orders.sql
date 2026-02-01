-- V9__create_orders.sql
-- Create orders table

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id UUID REFERENCES customers(id),
    address_id UUID REFERENCES addresses(id),
    coupon_id UUID REFERENCES coupons(id),
    customer_name VARCHAR(200),
    customer_phone VARCHAR(20),
    order_type VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(12,2) NOT NULL,
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    cancel_reason TEXT,
    estimated_delivery TIMESTAMP,
    delivered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_coupon ON orders(coupon_id);
