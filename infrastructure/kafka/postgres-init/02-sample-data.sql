-- 🗄️ Sample Data for Debezium CDC Demonstration (PostgreSQL)
-- Database: inventory
-- Purpose: Insert sample data to test Change Data Capture

-- 🛒 Insert Sample Customers
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('Juan', 'Pérez', 'juan.perez@email.com', '+34 600 123 456', 'Calle Mayor 123, Madrid'),
('María', 'García', 'maria.garcia@email.com', '+34 600 234 567', 'Avenida de la Paz 45, Barcelona'),
('Carlos', 'López', 'carlos.lopez@email.com', '+34 600 345 678', 'Plaza España 78, Valencia'),
('Ana', 'Martínez', 'ana.martinez@email.com', '+34 600 456 789', 'Calle Real 12, Sevilla'),
('Luis', 'Rodríguez', 'luis.rodriguez@email.com', '+34 600 567 890', 'Gran Vía 34, Bilbao');

-- 🍕 Insert Sample Products
INSERT INTO products (name, description, price, category, stock_quantity) VALUES
('Pizza Margherita', 'Pizza tradicional con tomate, mozzarella y albahaca', 12.99, 'Pizza', 50),
('Pizza Pepperoni', 'Pizza con pepperoni, mozzarella y salsa de tomate', 14.99, 'Pizza', 45),
('Hamburguesa Clásica', 'Hamburguesa con carne, lechuga, tomate y queso', 9.99, 'Hamburguesas', 60),
('Hamburguesa Vegetariana', 'Hamburguesa de garbanzos con verduras frescas', 11.99, 'Hamburguesas', 30),
('Ensalada César', 'Ensalada con lechuga, crutones, parmesano y aderezo César', 8.99, 'Ensaladas', 25),
('Pasta Carbonara', 'Pasta con salsa carbonara, panceta y parmesano', 13.99, 'Pasta', 40),
('Sushi California Roll', 'Roll de sushi con aguacate, pepino y cangrejo', 16.99, 'Sushi', 35),
('Paella Valenciana', 'Paella tradicional con arroz, pollo y mariscos', 18.99, 'Paella', 20);

-- 🏪 Insert Sample Restaurants
INSERT INTO restaurants (name, address, phone, email, cuisine_type) VALUES
('Pizzeria Bella Italia', 'Calle Italia 15, Madrid', '+34 91 123 4567', 'info@bellaitalia.com', 'Italiana'),
('Burger House', 'Avenida América 67, Madrid', '+34 91 234 5678', 'contact@burgerhouse.com', 'Americana'),
('Sushi Master', 'Calle Japón 23, Madrid', '+34 91 345 6789', 'reservas@sushimaster.com', 'Japonesa'),
('La Paella Valenciana', 'Plaza Mayor 8, Madrid', '+34 91 456 7890', 'info@paellavalenciana.com', 'Española'),
('Pasta Paradise', 'Gran Vía 156, Madrid', '+34 91 567 8901', 'hello@pastaparadise.com', 'Italiana');

-- 🔗 Insert Restaurant Products (Many-to-Many)
INSERT INTO restaurant_products (restaurant_id, product_id, price) VALUES
-- Pizzeria Bella Italia
(1, 1, 13.99), -- Pizza Margherita
(1, 2, 15.99), -- Pizza Pepperoni
(1, 6, 14.99), -- Pasta Carbonara

-- Burger House
(2, 3, 10.99), -- Hamburguesa Clásica
(2, 4, 12.99), -- Hamburguesa Vegetariana
(2, 5, 9.99),  -- Ensalada César

-- Sushi Master
(3, 7, 17.99), -- Sushi California Roll
(3, 5, 10.99), -- Ensalada César

-- La Paella Valenciana
(4, 8, 19.99), -- Paella Valenciana
(4, 5, 9.99),  -- Ensalada César

-- Pasta Paradise
(5, 6, 15.99), -- Pasta Carbonara
(5, 5, 9.99);  -- Ensalada César

-- 📋 Insert Sample Orders
INSERT INTO orders (customer_id, status, total_amount, delivery_address, notes) VALUES
(1, 'CONFIRMED', 27.98, 'Calle Mayor 123, Madrid', 'Sin cebolla en la pizza'),
(2, 'PREPARING', 32.97, 'Avenida de la Paz 45, Barcelona', 'Extra queso en la hamburguesa'),
(3, 'PENDING', 18.99, 'Plaza España 78, Valencia', 'Paella para 2 personas'),
(4, 'READY', 25.98, 'Calle Real 12, Sevilla', 'Sushi fresco'),
(5, 'DELIVERED', 22.98, 'Gran Vía 34, Bilbao', 'Pasta al dente');

-- 🛍️ Insert Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
-- Order 1: Juan Pérez
(1, 1, 1, 13.99, 13.99), -- Pizza Margherita
(1, 5, 1, 8.99, 8.99),   -- Ensalada César
(1, 3, 1, 10.99, 10.99), -- Hamburguesa Clásica

-- Order 2: María García
(2, 2, 1, 15.99, 15.99), -- Pizza Pepperoni
(2, 4, 1, 12.99, 12.99), -- Hamburguesa Vegetariana
(2, 5, 1, 9.99, 9.99),   -- Ensalada César

-- Order 3: Carlos López
(3, 8, 1, 18.99, 18.99), -- Paella Valenciana

-- Order 4: Ana Martínez
(4, 7, 1, 16.99, 16.99), -- Sushi California Roll
(4, 5, 1, 8.99, 8.99),   -- Ensalada César

-- Order 5: Luis Rodríguez
(5, 6, 1, 13.99, 13.99), -- Pasta Carbonara
(5, 5, 1, 8.99, 8.99);   -- Ensalada César

-- 💳 Insert Sample Payments
INSERT INTO payments (order_id, amount, payment_method, status, transaction_id) VALUES
(1, 27.98, 'CREDIT_CARD', 'COMPLETED', 'TXN_001_2024_001'),
(2, 32.97, 'DEBIT_CARD', 'COMPLETED', 'TXN_002_2024_001'),
(3, 18.99, 'CASH', 'COMPLETED', 'CASH_003_2024_001'),
(4, 25.98, 'PAYPAL', 'COMPLETED', 'PAY_004_2024_001'),
(5, 22.98, 'CREDIT_CARD', 'COMPLETED', 'TXN_005_2024_001');

-- 📊 Update order totals to match actual order items
UPDATE orders SET total_amount = (
    SELECT SUM(subtotal) FROM order_items WHERE order_id = orders.id
);

-- 🔍 Verify data insertion
SELECT 'Customers' as table_name, COUNT(*) as count FROM customers
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Restaurants', COUNT(*) FROM restaurants
UNION ALL
SELECT 'Restaurant Products', COUNT(*) FROM restaurant_products
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'Order Items', COUNT(*) FROM order_items
UNION ALL
SELECT 'Payments', COUNT(*) FROM payments;
