-- Inserting sample data into food_types table
INSERT INTO food_types (type_id, type_name) VALUES
(1, 'Appetizer'),
(2, 'Main Course'),
(3, 'Dessert');

-- Inserting sample data into food_items table
INSERT INTO food_items (item_id, item_name, price) VALUES
(1, 'Chicken Curry', 12.99),
(2, 'Chocolate Cake', 6.49),
(3, 'Rice Pudding', 4.99),
(4, 'Chicken Tikka', 10.99),
(5, 'Chocolate Ice Cream', 3.99);

-- Inserting sample data into customers table
INSERT INTO customers (customer_id, first_name, last_name, email, phone_number, address) VALUES
(1, 'John', 'Doe', 'john.doe@example.com', '123-456-7890', '123 Main St, City, Country'),
(2, 'Jane', 'Smith', 'jane.smith@example.com', '987-654-3210', '456 Elm St, Town, Country'),
(3, 'Alice', 'Johnson', 'alice.johnson@example.com', '555-123-4567', '789 Oak St, Village, Country');

-- Inserting sample data into customer_orders table
INSERT INTO customer_orders ( customer_id, order_date, total_amount) VALUES
( 1, '2024-05-01', 29.47),
( 2, '2024-05-02', 10.99),
( 3, '2024-05-03', 24.47);


~                             