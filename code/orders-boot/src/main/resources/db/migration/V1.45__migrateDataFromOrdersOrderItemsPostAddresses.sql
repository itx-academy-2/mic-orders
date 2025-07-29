CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO post_addresses_v2 (
    id,
    delivery_method,
    city,
    department,
    account_id,
    recipient_first_name,
    recipient_last_name,
    recipient_phone,
    title
)
SELECT
    pa.id,
    pa.delivery_method,
    pa.city,
    pa.department,
    o.account_id,
    o.first_name,
    o.last_name,
    'no phone',
    'temp: ' || gen_random_uuid()
FROM post_addresses pa
JOIN orders o ON pa.id = o.id;

INSERT INTO orders_v2 (
    id,
    is_paid,
    order_status,
    created_at,
    edited_at,
    account_id,
    post_address_v2_id
)
SELECT
    o.id,
    o.is_paid,
    o.order_status,
    o.created_at,
    o.edited_at,
    o.account_id,
    o.id
FROM orders o;

INSERT INTO order_items_v2 (
    order_v2_id,
    product_id,
    price,
    discount,
    quantity
)
SELECT
    oi.order_id,
    oi.product_id,
    oi.price,
    oi.discount,
    oi.quantity
FROM order_items oi;