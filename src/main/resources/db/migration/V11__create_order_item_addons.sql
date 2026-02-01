-- V11__create_order_item_addons.sql
-- Create order item addons table

CREATE TABLE order_item_addons (
    id UUID PRIMARY KEY,
    order_item_id UUID NOT NULL REFERENCES order_items(id) ON DELETE CASCADE,
    addon_id UUID REFERENCES addons(id),
    addon_name VARCHAR(100) NOT NULL,
    addon_price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_item_addons_item ON order_item_addons(order_item_id);
