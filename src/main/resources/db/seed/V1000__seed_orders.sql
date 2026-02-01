-- ========================================
-- Script de Pedidos de Teste - Cardápio Pro
-- ========================================
-- Execute APÓS o V999__seed_data.sql
-- Inclui: Orders, OrderItems, OrderItemAddons
-- ========================================

-- ========================================
-- 1. PEDIDOS
-- ========================================

INSERT INTO orders (
    id, order_number, customer_name, customer_phone, customer_email, 
    delivery_address, delivery_complement, delivery_neighborhood,
    order_type, status, payment_method, payment_status,
    subtotal, delivery_fee, discount, total, change_for,
    notes, estimated_time, created_at, updated_at
) VALUES
-- Pedido 1: PENDING (Delivery) - João Silva
('11111111-0001-0001-0001-000000000001', 1001, 'João Silva', '11999999999', 'joao@email.com',
 'Rua das Flores, 123', 'Apto 101', 'Centro',
 'DELIVERY', 'PENDING', 'CREDIT_CARD', 'PENDING',
 28.90, 5.00, 0.00, 33.90, NULL,
 'Sem cebola', 30, NOW(), NOW()),

-- Pedido 2: CONFIRMED (Pickup) - Maria Oliveira
('11111111-0001-0001-0001-000000000002', 1002, 'Maria Oliveira', '11888888888', 'maria@email.com',
 NULL, NULL, NULL,
 'PICKUP', 'CONFIRMED', 'PIX', 'PAID',
 57.90, 0.00, 0.00, 57.90, NULL,
 NULL, 40, NOW() - INTERVAL '1 hour', NOW()),

-- Pedido 3: DELIVERED (Delivery) - Carlos Souza
('11111111-0001-0001-0001-000000000003', 1003, 'Carlos Souza', '11777777777', NULL,
 'Av. Paulista, 1000', NULL, 'Bela Vista',
 'DELIVERY', 'DELIVERED', 'CASH', 'PAID',
 59.80, 5.00, 0.00, 64.80, 100.00,
 'Troco para 100', 45, NOW() - INTERVAL '2 days', NOW());

-- ========================================
-- 2. ITENS DO PEDIDO
-- ========================================

INSERT INTO order_items (
    id, order_id, product_id, product_name, quantity, unit_price, notes, subtotal
) VALUES
-- Pedido 1: 1x X-Burger Clássico
('22222222-0001-0001-0001-000000000001', '11111111-0001-0001-0001-000000000001', 
 'a1000001-0001-0001-0001-000000000001', 'X-Burger Clássico', 1, 28.90, 'Sem cebola', 28.90),

-- Pedido 2: 1x Pizza Margherita
('22222222-0001-0001-0001-000000000002', '11111111-0001-0001-0001-000000000002', 
 'a2000002-0002-0002-0002-000000000001', 'Pizza Margherita', 1, 49.90, NULL, 57.90), -- 49.90 + 8.00 (borda)

-- Pedido 3: 2x X-Bacon
('22222222-0001-0001-0001-000000000003', '11111111-0001-0001-0001-000000000003', 
 'a1000001-0001-0001-0001-000000000002', 'X-Bacon', 2, 29.90, NULL, 59.80); -- 29.90 * 2

-- ========================================
-- 3. ADICIONAIS DOS ITENS
-- ========================================

INSERT INTO order_item_addons (
    id, order_item_id, addon_id, addon_name, quantity, price
) VALUES
-- Pedido 2 (Pizza): Borda Catupiry
('33333333-0001-0001-0001-000000000001', '22222222-0001-0001-0001-000000000002', 
 'd6000006-0006-0006-0006-000000000002', 'Borda Recheada Catupiry', 1, 8.00);

-- ========================================
-- VERIFICAÇÕES
-- ========================================

SELECT 'Pedidos inseridos:' as info, COUNT(*) as total FROM orders;
SELECT 'Itens inseridos:' as info, COUNT(*) as total FROM order_items;
SELECT 'Adicionais de itens inseridos:' as info, COUNT(*) as total FROM order_item_addons;
