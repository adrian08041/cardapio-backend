-- V6__create_customers.sql
-- Create customers table

CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255),
    cpf VARCHAR(14),
    birth_date DATE,
    tags TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    total_orders INTEGER NOT NULL DEFAULT 0,
    total_spent DECIMAL(12,2) NOT NULL DEFAULT 0,
    average_ticket DECIMAL(10,2) NOT NULL DEFAULT 0,
    last_order_date TIMESTAMP,
    first_order_date TIMESTAMP,
    loyalty_points INTEGER NOT NULL DEFAULT 0,
    lifetime_points INTEGER NOT NULL DEFAULT 0,
    loyalty_tier VARCHAR(20) NOT NULL DEFAULT 'BRONZE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_loyalty_tier ON customers(loyalty_tier);
