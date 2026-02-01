-- ========================================
-- Script Completo de Dados - Cardápio Pro
-- ========================================
-- Execute este script no Beekeeper ou PgAdmin
-- Inclui: Categories, Products, AddonCategories, Addons
-- ========================================

-- ========================================
-- LIMPAR DADOS (Descomente se necessário)
-- ========================================
-- TRUNCATE TABLE product_addon_categories, addons, addon_categories, products, categories RESTART IDENTITY CASCADE;

-- ========================================
-- 1. CATEGORIAS DE CARDÁPIO
-- ========================================

INSERT INTO categories (id, name, slug, icon, display_order, active, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Hambúrgueres', 'hamburgueres', '🍔', 1, true, NOW(), NOW()),
('22222222-2222-2222-2222-222222222222', 'Pizzas', 'pizzas', '🍕', 2, true, NOW(), NOW()),
('33333333-3333-3333-3333-333333333333', 'Bebidas', 'bebidas', '🥤', 3, true, NOW(), NOW()),
('44444444-4444-4444-4444-444444444444', 'Sobremesas', 'sobremesas', '🍰', 4, true, NOW(), NOW()),
('55555555-5555-5555-5555-555555555555', 'Porções', 'porcoes', '🍟', 5, true, NOW(), NOW()),
('66666666-6666-6666-6666-666666666666', 'Combos', 'combos', '🎁', 6, true, NOW(), NOW()),
('77777777-7777-7777-7777-777777777777', 'Saladas', 'saladas', '🥗', 7, true, NOW(), NOW()),
('88888888-8888-8888-8888-888888888888', 'Massas', 'massas', '🍝', 8, true, NOW(), NOW());

-- ========================================
-- 2. PRODUTOS - Hambúrgueres
-- ========================================

INSERT INTO products (id, name, slug, description, price, promotional_price, image_url, preparation_time, serves, available, active, display_order, category_id, created_at, updated_at) VALUES
('a1000001-0001-0001-0001-000000000001', 'X-Burger Clássico', 'x-burger-classico', 'Pão brioche, hambúrguer 180g, queijo cheddar, alface, tomate e molho especial', 28.90, NULL, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400', 15, 1, true, true, 1, '11111111-1111-1111-1111-111111111111', NOW(), NOW()),
('a1000001-0001-0001-0001-000000000002', 'X-Bacon', 'x-bacon', 'Pão brioche, hambúrguer 180g, bacon crocante, queijo cheddar e molho barbecue', 32.90, 29.90, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=400', 18, 1, true, true, 2, '11111111-1111-1111-1111-111111111111', NOW(), NOW()),
('a1000001-0001-0001-0001-000000000003', 'X-Salada', 'x-salada', 'Pão integral, hambúrguer 180g, queijo branco, alface, tomate, cebola roxa', 26.90, NULL, 'https://images.unsplash.com/photo-1572802419224-296b0aeee0d9?w=400', 15, 1, true, true, 3, '11111111-1111-1111-1111-111111111111', NOW(), NOW()),
('a1000001-0001-0001-0001-000000000004', 'X-Tudo', 'x-tudo', 'Pão brioche, 2 hambúrgueres, bacon, ovo, queijo, presunto, alface, tomate', 42.90, NULL, 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=400', 25, 1, true, true, 4, '11111111-1111-1111-1111-111111111111', NOW(), NOW()),
('a1000001-0001-0001-0001-000000000005', 'Veggie Burger', 'veggie-burger', 'Pão integral, hambúrguer de grão de bico, queijo vegano, legumes grelhados', 34.90, NULL, 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=400', 20, 1, true, true, 5, '11111111-1111-1111-1111-111111111111', NOW(), NOW()),
('a1000001-0001-0001-0001-000000000006', 'X-Picanha', 'x-picanha', 'Pão brioche, hambúrguer de picanha 200g, queijo provolone, cebola caramelizada', 45.90, NULL, 'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?w=400', 20, 1, true, true, 6, '11111111-1111-1111-1111-111111111111', NOW(), NOW());

-- ========================================
-- 2. PRODUTOS - Pizzas
-- ========================================

INSERT INTO products (id, name, slug, description, price, promotional_price, image_url, preparation_time, serves, available, active, display_order, category_id, created_at, updated_at) VALUES
('a2000002-0002-0002-0002-000000000001', 'Pizza Margherita', 'pizza-margherita', 'Molho de tomate, mussarela de búfala, manjericão fresco e azeite', 49.90, NULL, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400', 30, 2, true, true, 1, '22222222-2222-2222-2222-222222222222', NOW(), NOW()),
('a2000002-0002-0002-0002-000000000002', 'Pizza Calabresa', 'pizza-calabresa', 'Molho de tomate, mussarela, calabresa fatiada e cebola', 45.90, 39.90, 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400', 30, 2, true, true, 2, '22222222-2222-2222-2222-222222222222', NOW(), NOW()),
('a2000002-0002-0002-0002-000000000003', 'Pizza Quatro Queijos', 'pizza-quatro-queijos', 'Mussarela, gorgonzola, parmesão e catupiry', 54.90, NULL, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400', 30, 2, true, true, 3, '22222222-2222-2222-2222-222222222222', NOW(), NOW()),
('a2000002-0002-0002-0002-000000000004', 'Pizza Portuguesa', 'pizza-portuguesa', 'Molho, mussarela, presunto, ovos, cebola, azeitona e pimentão', 52.90, NULL, 'https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?w=400', 35, 2, true, true, 4, '22222222-2222-2222-2222-222222222222', NOW(), NOW()),
('a2000002-0002-0002-0002-000000000005', 'Pizza Pepperoni', 'pizza-pepperoni', 'Molho de tomate, mussarela e pepperoni artesanal', 55.90, NULL, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400', 30, 2, true, true, 5, '22222222-2222-2222-2222-222222222222', NOW(), NOW());

-- ========================================
-- 2. PRODUTOS - Bebidas
-- ========================================

INSERT INTO products (id, name, slug, description, price, promotional_price, image_url, preparation_time, serves, available, active, display_order, category_id, created_at, updated_at) VALUES
('a3000003-0003-0003-0003-000000000001', 'Refrigerante Lata 350ml', 'refrigerante-lata', 'Coca-Cola, Guaraná Antarctica ou Sprite', 6.90, NULL, 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e?w=400', 1, 1, true, true, 1, '33333333-3333-3333-3333-333333333333', NOW(), NOW()),
('a3000003-0003-0003-0003-000000000002', 'Suco Natural 400ml', 'suco-natural', 'Laranja, limão, abacaxi ou maracujá', 9.90, NULL, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400', 5, 1, true, true, 2, '33333333-3333-3333-3333-333333333333', NOW(), NOW()),
('a3000003-0003-0003-0003-000000000003', 'Água Mineral 500ml', 'agua-mineral', 'Com ou sem gás', 4.90, NULL, 'https://images.unsplash.com/photo-1548839140-29a749e1cf4d?w=400', 1, 1, true, true, 3, '33333333-3333-3333-3333-333333333333', NOW(), NOW()),
('a3000003-0003-0003-0003-000000000004', 'Milkshake 400ml', 'milkshake', 'Chocolate, morango, baunilha ou Ovomaltine', 16.90, 14.90, 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=400', 8, 1, true, true, 4, '33333333-3333-3333-3333-333333333333', NOW(), NOW()),
('a3000003-0003-0003-0003-000000000005', 'Cerveja Artesanal 500ml', 'cerveja-artesanal', 'IPA, Pilsen ou Weiss - sob consulta', 18.90, NULL, 'https://images.unsplash.com/photo-1608270586620-248524c67de9?w=400', 1, 1, true, true, 5, '33333333-3333-3333-3333-333333333333', NOW(), NOW()),
('a3000003-0003-0003-0003-000000000006', 'Chá Gelado 300ml', 'cha-gelado', 'Limão, pêssego ou frutas vermelhas', 7.90, NULL, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 2, 1, true, true, 6, '33333333-3333-3333-3333-333333333333', NOW(), NOW());

-- ========================================
-- 2. PRODUTOS - Sobremesas
-- ========================================

INSERT INTO products (id, name, slug, description, price, promotional_price, image_url, preparation_time, serves, available, active, display_order, category_id, created_at, updated_at) VALUES
('a4000004-0004-0004-0004-000000000001', 'Brownie com Sorvete', 'brownie-sorvete', 'Brownie de chocolate belga com sorvete de creme e calda quente', 22.90, NULL, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400', 10, 1, true, true, 1, '44444444-4444-4444-4444-444444444444', NOW(), NOW()),
('a4000004-0004-0004-0004-000000000002', 'Petit Gateau', 'petit-gateau', 'Bolinho de chocolate com recheio cremoso e sorvete de baunilha', 26.90, NULL, 'https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=400', 15, 1, true, true, 2, '44444444-4444-4444-4444-444444444444', NOW(), NOW()),
('a4000004-0004-0004-0004-000000000003', 'Cheesecake', 'cheesecake', 'Cheesecake cremoso com calda de frutas vermelhas', 19.90, NULL, 'https://images.unsplash.com/photo-1567327613485-fbc7bf196198?w=400', 5, 1, true, true, 3, '44444444-4444-4444-4444-444444444444', NOW(), NOW()),
('a4000004-0004-0004-0004-000000000004', 'Açaí 500ml', 'acai', 'Açaí batido com banana, granola, leite em pó e mel', 24.90, 21.90, 'https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400', 5, 1, true, true, 4, '44444444-4444-4444-4444-444444444444', NOW(), NOW());

-- ========================================
-- 2. PRODUTOS - Porções
-- ========================================

INSERT INTO products (id, name, slug, description, price, promotional_price, image_url, preparation_time, serves, available, active, display_order, category_id, created_at, updated_at) VALUES
('a5000005-0005-0005-0005-000000000001', 'Batata Frita', 'batata-frita', 'Porção de batata frita crocante com cheddar e bacon (opcional)', 24.90, NULL, 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400', 12, 2, true, true, 1, '55555555-5555-5555-5555-555555555555', NOW(), NOW()),
('a5000005-0005-0005-0005-000000000002', 'Onion Rings', 'onion-rings', 'Anéis de cebola empanados e fritos com molho especial', 22.90, NULL, 'https://images.unsplash.com/photo-1639024471283-03518883512d?w=400', 10, 2, true, true, 2, '55555555-5555-5555-5555-555555555555', NOW(), NOW()),
('a5000005-0005-0005-0005-000000000003', 'Nuggets de Frango', 'nuggets-frango', '12 unidades de nuggets crocantes com molhos à escolha', 28.90, 24.90, 'https://images.unsplash.com/photo-1562967914-608f82629710?w=400', 12, 2, true, true, 3, '55555555-5555-5555-5555-555555555555', NOW(), NOW()),
('a5000005-0005-0005-0005-000000000004', 'Calabresa Acebolada', 'calabresa-acebolada', 'Calabresa artesanal fatiada com cebola caramelizada', 32.90, NULL, 'https://images.unsplash.com/photo-1599921841143-819065a55cc6?w=400', 15, 3, true, true, 4, '55555555-5555-5555-5555-555555555555', NOW(), NOW());

-- ========================================
-- 3. CATEGORIAS DE ADICIONAIS
-- ========================================

INSERT INTO addon_categories (id, name, description, min_selection, max_selection, required, display_order, active, created_at, updated_at) VALUES
('c1000001-0001-0001-0001-000000000001', 'Queijos', 'Escolha os queijos extras', 0, 3, false, 1, true, NOW(), NOW()),
('c1000001-0001-0001-0001-000000000002', 'Proteínas', 'Adicione mais proteínas', 0, 2, false, 2, true, NOW(), NOW()),
('c1000001-0001-0001-0001-000000000003', 'Molhos', 'Escolha seus molhos favoritos', 0, 3, false, 3, true, NOW(), NOW()),
('c1000001-0001-0001-0001-000000000004', 'Extras', 'Adicionais especiais', 0, 5, false, 4, true, NOW(), NOW()),
('c1000001-0001-0001-0001-000000000005', 'Tamanho', 'Escolha o tamanho', 1, 1, true, 5, true, NOW(), NOW()),
('c1000001-0001-0001-0001-000000000006', 'Borda', 'Escolha a borda da pizza', 0, 1, false, 6, true, NOW(), NOW());

-- ========================================
-- 4. ADICIONAIS - Queijos
-- ========================================

INSERT INTO addons (id, name, description, price, max_quantity, available, active, display_order, addon_category_id, created_at, updated_at) VALUES
('d1000001-0001-0001-0001-000000000001', 'Queijo Cheddar', 'Fatia extra de queijo cheddar', 3.00, 3, true, true, 1, 'c1000001-0001-0001-0001-000000000001', NOW(), NOW()),
('d1000001-0001-0001-0001-000000000002', 'Queijo Prato', 'Fatia de queijo prato derretido', 2.50, 3, true, true, 2, 'c1000001-0001-0001-0001-000000000001', NOW(), NOW()),
('d1000001-0001-0001-0001-000000000003', 'Queijo Gorgonzola', 'Porção de queijo gorgonzola', 5.00, 2, true, true, 3, 'c1000001-0001-0001-0001-000000000001', NOW(), NOW()),
('d1000001-0001-0001-0001-000000000004', 'Catupiry', 'Porção generosa de catupiry', 4.00, 2, true, true, 4, 'c1000001-0001-0001-0001-000000000001', NOW(), NOW()),
('d1000001-0001-0001-0001-000000000005', 'Mussarela de Búfala', 'Mussarela de búfala premium', 6.00, 2, true, true, 5, 'c1000001-0001-0001-0001-000000000001', NOW(), NOW());

-- ========================================
-- 4. ADICIONAIS - Proteínas
-- ========================================

INSERT INTO addons (id, name, description, price, max_quantity, available, active, display_order, addon_category_id, created_at, updated_at) VALUES
('d2000002-0002-0002-0002-000000000001', 'Bacon Extra', 'Porção extra de bacon crocante', 5.00, 2, true, true, 1, 'c1000001-0001-0001-0001-000000000002', NOW(), NOW()),
('d2000002-0002-0002-0002-000000000002', 'Ovo', 'Ovo frito ou mexido', 3.00, 2, true, true, 2, 'c1000001-0001-0001-0001-000000000002', NOW(), NOW()),
('d2000002-0002-0002-0002-000000000003', 'Hambúrguer Extra', 'Mais um hambúrguer 120g', 12.00, 2, true, true, 3, 'c1000001-0001-0001-0001-000000000002', NOW(), NOW()),
('d2000002-0002-0002-0002-000000000004', 'Presunto', 'Fatias de presunto de qualidade', 4.00, 2, true, true, 4, 'c1000001-0001-0001-0001-000000000002', NOW(), NOW()),
('d2000002-0002-0002-0002-000000000005', 'Calabresa', 'Rodelas de calabresa', 5.00, 2, true, true, 5, 'c1000001-0001-0001-0001-000000000002', NOW(), NOW());

-- ========================================
-- 4. ADICIONAIS - Molhos
-- ========================================

INSERT INTO addons (id, name, description, price, max_quantity, available, active, display_order, addon_category_id, created_at, updated_at) VALUES
('d3000003-0003-0003-0003-000000000001', 'Molho Barbecue', 'Molho barbecue defumado', 2.00, 3, true, true, 1, 'c1000001-0001-0001-0001-000000000003', NOW(), NOW()),
('d3000003-0003-0003-0003-000000000002', 'Maionese Especial', 'Maionese da casa temperada', 2.00, 3, true, true, 2, 'c1000001-0001-0001-0001-000000000003', NOW(), NOW()),
('d3000003-0003-0003-0003-000000000003', 'Mostarda', 'Mostarda tradicional', 1.50, 3, true, true, 3, 'c1000001-0001-0001-0001-000000000003', NOW(), NOW()),
('d3000003-0003-0003-0003-000000000004', 'Ketchup', 'Ketchup premium', 1.50, 3, true, true, 4, 'c1000001-0001-0001-0001-000000000003', NOW(), NOW()),
('d3000003-0003-0003-0003-000000000005', 'Molho de Alho', 'Creme de alho artesanal', 3.00, 2, true, true, 5, 'c1000001-0001-0001-0001-000000000003', NOW(), NOW()),
('d3000003-0003-0003-0003-000000000006', 'Molho Ranch', 'Molho ranch cremoso', 3.00, 2, true, true, 6, 'c1000001-0001-0001-0001-000000000003', NOW(), NOW());

-- ========================================
-- 4. ADICIONAIS - Extras
-- ========================================

INSERT INTO addons (id, name, description, price, max_quantity, available, active, display_order, addon_category_id, created_at, updated_at) VALUES
('d4000004-0004-0004-0004-000000000001', 'Cebola Caramelizada', 'Cebola caramelizada na manteiga', 4.00, 2, true, true, 1, 'c1000001-0001-0001-0001-000000000004', NOW(), NOW()),
('d4000004-0004-0004-0004-000000000002', 'Jalapeño', 'Pimenta jalapeño em rodelas', 3.00, 2, true, true, 2, 'c1000001-0001-0001-0001-000000000004', NOW(), NOW()),
('d4000004-0004-0004-0004-000000000003', 'Cogumelos', 'Cogumelos salteados', 6.00, 2, true, true, 3, 'c1000001-0001-0001-0001-000000000004', NOW(), NOW()),
('d4000004-0004-0004-0004-000000000004', 'Rúcula', 'Folhas frescas de rúcula', 3.00, 2, true, true, 4, 'c1000001-0001-0001-0001-000000000004', NOW(), NOW()),
('d4000004-0004-0004-0004-000000000005', 'Tomate Seco', 'Tomate seco em conserva', 5.00, 2, true, true, 5, 'c1000001-0001-0001-0001-000000000004', NOW(), NOW());

-- ========================================
-- 4. ADICIONAIS - Tamanho
-- ========================================

INSERT INTO addons (id, name, description, price, max_quantity, available, active, display_order, addon_category_id, created_at, updated_at) VALUES
('d5000005-0005-0005-0005-000000000001', 'Pequeno', 'Porção individual', 0.00, 1, true, true, 1, 'c1000001-0001-0001-0001-000000000005', NOW(), NOW()),
('d5000005-0005-0005-0005-000000000002', 'Médio', 'Serve 2 pessoas', 8.00, 1, true, true, 2, 'c1000001-0001-0001-0001-000000000005', NOW(), NOW()),
('d5000005-0005-0005-0005-000000000003', 'Grande', 'Serve 3-4 pessoas', 15.00, 1, true, true, 3, 'c1000001-0001-0001-0001-000000000005', NOW(), NOW());

-- ========================================
-- 4. ADICIONAIS - Borda (Pizza)
-- ========================================

INSERT INTO addons (id, name, description, price, max_quantity, available, active, display_order, addon_category_id, created_at, updated_at) VALUES
('d6000006-0006-0006-0006-000000000001', 'Borda Tradicional', 'Borda simples crocante', 0.00, 1, true, true, 1, 'c1000001-0001-0001-0001-000000000006', NOW(), NOW()),
('d6000006-0006-0006-0006-000000000002', 'Borda Recheada Catupiry', 'Borda com catupiry cremoso', 8.00, 1, true, true, 2, 'c1000001-0001-0001-0001-000000000006', NOW(), NOW()),
('d6000006-0006-0006-0006-000000000003', 'Borda Recheada Cheddar', 'Borda com cheddar derretido', 8.00, 1, true, true, 3, 'c1000001-0001-0001-0001-000000000006', NOW(), NOW()),
('d6000006-0006-0006-0006-000000000004', 'Borda Chocolate', 'Borda com chocolate (sobremesa)', 10.00, 1, true, true, 4, 'c1000001-0001-0001-0001-000000000006', NOW(), NOW());

-- ========================================
-- 5. VÍNCULOS PRODUTO <-> CATEGORIA DE ADDON
-- ========================================

-- Hambúrgueres podem ter: Queijos, Proteínas, Molhos, Extras
INSERT INTO product_addon_categories (id, product_id, addon_category_id, display_order) VALUES
-- X-Burger Clássico
('e1000001-0001-0001-0001-000000000001', 'a1000001-0001-0001-0001-000000000001', 'c1000001-0001-0001-0001-000000000001', 1),
('e1000001-0001-0001-0001-000000000002', 'a1000001-0001-0001-0001-000000000001', 'c1000001-0001-0001-0001-000000000002', 2),
('e1000001-0001-0001-0001-000000000003', 'a1000001-0001-0001-0001-000000000001', 'c1000001-0001-0001-0001-000000000003', 3),
('e1000001-0001-0001-0001-000000000004', 'a1000001-0001-0001-0001-000000000001', 'c1000001-0001-0001-0001-000000000004', 4),
-- X-Bacon
('e1000001-0001-0001-0001-000000000005', 'a1000001-0001-0001-0001-000000000002', 'c1000001-0001-0001-0001-000000000001', 1),
('e1000001-0001-0001-0001-000000000006', 'a1000001-0001-0001-0001-000000000002', 'c1000001-0001-0001-0001-000000000002', 2),
('e1000001-0001-0001-0001-000000000007', 'a1000001-0001-0001-0001-000000000002', 'c1000001-0001-0001-0001-000000000003', 3);

-- Pizzas podem ter: Borda, Extras
INSERT INTO product_addon_categories (id, product_id, addon_category_id, display_order) VALUES
-- Pizza Margherita
('e2000002-0002-0002-0002-000000000001', 'a2000002-0002-0002-0002-000000000001', 'c1000001-0001-0001-0001-000000000006', 1),
('e2000002-0002-0002-0002-000000000002', 'a2000002-0002-0002-0002-000000000001', 'c1000001-0001-0001-0001-000000000004', 2),
-- Pizza Calabresa
('e2000002-0002-0002-0002-000000000003', 'a2000002-0002-0002-0002-000000000002', 'c1000001-0001-0001-0001-000000000006', 1),
('e2000002-0002-0002-0002-000000000004', 'a2000002-0002-0002-0002-000000000002', 'c1000001-0001-0001-0001-000000000004', 2);

-- Porções podem ter: Tamanho, Molhos
INSERT INTO product_addon_categories (id, product_id, addon_category_id, display_order) VALUES
-- Batata Frita
('e3000003-0003-0003-0003-000000000001', 'a5000005-0005-0005-0005-000000000001', 'c1000001-0001-0001-0001-000000000005', 1),
('e3000003-0003-0003-0003-000000000002', 'a5000005-0005-0005-0005-000000000001', 'c1000001-0001-0001-0001-000000000003', 2);

-- ========================================
-- VERIFICAÇÕES
-- ========================================

SELECT 'Categorias inseridas:' as info, COUNT(*) as total FROM categories;
SELECT 'Produtos inseridos:' as info, COUNT(*) as total FROM products;
SELECT 'Categorias de Addon inseridas:' as info, COUNT(*) as total FROM addon_categories;
SELECT 'Addons inseridos:' as info, COUNT(*) as total FROM addons;
SELECT 'Vínculos Produto-AddonCategory inseridos:' as info, COUNT(*) as total FROM product_addon_categories;
