-- V3__create_addon_categories.sql
-- Create addon categories table

CREATE TABLE addon_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    min_selections INTEGER NOT NULL DEFAULT 0,
    max_selections INTEGER NOT NULL DEFAULT 1,
    required BOOLEAN NOT NULL DEFAULT false,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_addon_categories_display_order ON addon_categories(display_order);
