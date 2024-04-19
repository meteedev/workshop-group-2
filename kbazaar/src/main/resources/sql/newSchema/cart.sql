CREATE TABLE IF NOT EXISTS cart (
    id SERIAL PRIMARY KEY,
    shopper_id INT NOT NULL REFERENCES shopper(id),
    product_id INT NOT NULL REFERENCES product(id),
    quantity INT NOT NULL
);