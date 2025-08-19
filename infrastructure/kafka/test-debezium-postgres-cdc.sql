-- 🧪 Test Script for Debezium PostgreSQL CDC
-- Purpose: Make changes to the PostgreSQL database to test Change Data Capture
-- Run this script after Debezium is running to see changes in Kafka topics

-- 📝 Test 1: Insert a new customer
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('Test', 'Customer', 'test.customer@email.com', '+34 600 999 999', 'Calle Test 999, Madrid');

-- 📝 Test 2: Update an existing customer
UPDATE customers 
SET phone = '+34 600 888 888', 
    address = 'Nueva Dirección 123, Madrid'
WHERE email = 'juan.perez@email.com';

-- 📝 Test 3: Insert a new product
INSERT INTO products (name, description, price, category, stock_quantity) VALUES
('Pizza Hawaiana', 'Pizza con jamón y piña', 16.99, 'Pizza', 25);

-- 📝 Test 4: Update product price
UPDATE products 
SET price = 15.99, 
    stock_quantity = 30
WHERE name = 'Pizza Margherita';

-- 📝 Test 5: Insert a new order
INSERT INTO orders (customer_id, status, total_amount, delivery_address, notes) VALUES
(6, 'PENDING', 0.00, 'Calle Test 999, Madrid', 'Orden de prueba para CDC');

-- 📝 Test 6: Insert order items for the new order
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(6, 1, 2, 15.99, 31.98),  -- 2x Pizza Margherita
(6, 5, 1, 8.99, 8.99);    -- 1x Ensalada César

-- 📝 Test 7: Update order total
UPDATE orders 
SET total_amount = 40.97, 
    status = 'CONFIRMED'
WHERE id = 6;

-- 📝 Test 8: Insert payment for the new order
INSERT INTO payments (order_id, amount, payment_method, status, transaction_id) VALUES
(6, 40.97, 'CREDIT_CARD', 'COMPLETED', 'TXN_006_2024_001');

-- 📝 Test 9: Update order status
UPDATE orders 
SET status = 'PREPARING'
WHERE id = 6;

-- 📝 Test 10: Delete a test record (this will create a tombstone in Kafka)
-- First, let's insert a test record to delete
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('Delete', 'Test', 'delete.test@email.com', '+34 600 777 777', 'Calle Delete 777, Madrid');

-- Now delete it
DELETE FROM customers WHERE email = 'delete.test@email.com';

-- 🔍 Verify the changes
SELECT 'Current Customers Count' as info, COUNT(*) as count FROM customers
UNION ALL
SELECT 'Current Products Count', COUNT(*) FROM products
UNION ALL
SELECT 'Current Orders Count', COUNT(*) FROM orders
UNION ALL
SELECT 'Current Payments Count', COUNT(*) FROM payments;

-- 📊 Show recent changes
SELECT 'Recent Customers' as table_name, id, first_name, last_name, email, created_at 
FROM customers 
ORDER BY created_at DESC 
LIMIT 3;

SELECT 'Recent Products' as table_name, id, name, price, category, updated_at 
FROM products 
ORDER BY updated_at DESC 
LIMIT 3;

SELECT 'Recent Orders' as table_name, id, customer_id, status, total_amount, updated_at 
FROM orders 
ORDER BY updated_at DESC 
LIMIT 3;
