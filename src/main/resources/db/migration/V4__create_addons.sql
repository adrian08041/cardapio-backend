-- V4__create_addons.sql
-- Create addons table

CREATE TABLE addons (
    id UUID PRIMARY KEY,
    addon_category_id UUID NOT NULL REFERENCES addon_categories(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_addons_category ON addons(addon_category_id);
CREATE INDEX idx_addons_available ON addons(available);
