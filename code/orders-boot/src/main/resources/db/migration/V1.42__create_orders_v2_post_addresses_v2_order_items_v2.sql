CREATE TABLE post_addresses_v2 (
    id UUID PRIMARY KEY,
    delivery_method VARCHAR(50) NOT NULL,
    city VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    account_id BIGINT NOT NULL,
    recipient_first_name VARCHAR(50) NOT NULL,
    recipient_last_name VARCHAR(50) NOT NULL,
    recipient_phone VARCHAR(13) NOT NULL,
    title VARCHAR(100) NOT NULL,
    CONSTRAINT fk_post_addresses_v2_account_id FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE orders_v2 (
    id UUID PRIMARY KEY,
    is_paid BOOLEAN NOT NULL,
    order_status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    edited_at TIMESTAMP NOT NULL DEFAULT now(),
    account_id BIGINT NOT NULL,
    post_address_v2_id UUID,
    CONSTRAINT FK_orders_v2_account_id FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT FK_orders_v2_post_address_v2_id FOREIGN KEY (post_address_v2_id) REFERENCES post_addresses_v2(id)
);

CREATE TABLE order_items_v2 (
    order_v2_id UUID NOT NULL,
    product_id UUID NOT NULL,
    price DECIMAL (10, 2) NOT NULL,
    discount INT DEFAULT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_v2_id, product_id),
    CONSTRAINT FK_order_items_v2_order FOREIGN KEY (order_V2_id) REFERENCES orders_v2(id),
    CONSTRAINT FK_order_items_v2_product FOREIGN KEY (product_id) REFERENCES products(id)
);