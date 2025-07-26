CREATE TABLE wishlist (
    account_id BIGINT NOT NULL,
    product_id UUID NOT NULL,
    added_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    PRIMARY KEY (account_id, product_id),
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_wishlist_account_id_added_at ON wishlist(account_id, added_at);