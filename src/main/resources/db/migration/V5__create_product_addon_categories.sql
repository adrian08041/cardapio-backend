-- V5__create_product_addon_categories.sql
-- Create junction table for products and addon categories

CREATE TABLE product_addon_categories (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    addon_category_id UUID NOT NULL REFERENCES addon_categories(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, addon_category_id)
);

CREATE INDEX idx_product_addon_categories_product ON product_addon_categories(product_id);
CREATE INDEX idx_product_addon_categories_addon ON product_addon_categories(addon_category_id);
