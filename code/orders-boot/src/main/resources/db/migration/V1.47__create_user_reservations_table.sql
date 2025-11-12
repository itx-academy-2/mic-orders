CREATE TABLE user_reservations (
    user_id BIGINT NOT NULL,
    product_id UUID NOT NULL,
    added_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_reservations_user_id_added_at ON user_reservations(user_id, added_at);
