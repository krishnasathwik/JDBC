-- Dropping sample data from customer_orders table
DELETE FROM customer_orders WHERE customer_id IN (1, 2, 3);

-- Dropping sample data from customers table
DELETE FROM customers WHERE customer_id IN (1, 2, 3);

-- Dropping sample data from food_items table
DELETE FROM food_items WHERE item_id IN (1, 2, 3, 4, 5);

-- Dropping sample data from food_types table
DELETE FROM food_types WHERE type_id IN (1, 2, 3);