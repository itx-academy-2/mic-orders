CREATE TABLE viewed_history (
    account_id BIGINT NOT NULL,
    product_id UUID NOT NULL,
    viewed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (account_id, product_id),
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- index for faster sorting by viewed_at (useful for "recently viewed" queries)
CREATE INDEX idx_viewed_history_viewed_at ON viewed_history(viewed_at DESC);