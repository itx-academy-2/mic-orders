-- Add quantity column with safe default for existing data
ALTER TABLE user_reservations
    ADD COLUMN quantity INT NOT NULL DEFAULT 1;

-- Ensure quantity is always positive
ALTER TABLE user_reservations
    ADD CONSTRAINT chk_user_reservations_quantity_positive
        CHECK (quantity > 0);