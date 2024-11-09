ALTER TABLE customers
ADD COLUMN date_of_birth DATE;

ALTER TABLE food_items
MODIFY COLUMN price DECIMAL(12, 2);

ALTER TABLE food_types
CHANGE COLUMN type_name category_name VARCHAR(50);

ALTER TABLE customer_orders
ADD CONSTRAINT fk_customer_id
FOREIGN KEY (customer_id) REFERENCES customers(customer_id);
