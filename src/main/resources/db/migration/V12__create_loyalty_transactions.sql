-- V12__create_loyalty_transactions.sql
-- Create loyalty transactions table

CREATE TABLE loyalty_transactions (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    order_id UUID REFERENCES orders(id),
    transaction_type VARCHAR(20) NOT NULL,
    points INTEGER NOT NULL,
    description VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loyalty_transactions_customer ON loyalty_transactions(customer_id);
CREATE INDEX idx_loyalty_transactions_order ON loyalty_transactions(order_id);
CREATE INDEX idx_loyalty_transactions_type ON loyalty_transactions(transaction_type);
CREATE INDEX idx_loyalty_transactions_created ON loyalty_transactions(created_at);
