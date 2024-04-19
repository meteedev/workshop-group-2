CREATE TABLE IF NOT EXISTS kpoint (
    id SERIAL PRIMARY KEY,
    shopper_id INT NOT NULL REFERENCES shopper(id),
    point INT NOT NULL
);