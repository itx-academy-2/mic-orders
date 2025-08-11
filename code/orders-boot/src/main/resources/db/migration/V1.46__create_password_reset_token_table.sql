CREATE SEQUENCE IF NOT EXISTS password_reset_token_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                                     id BIGINT PRIMARY KEY DEFAULT nextval('password_reset_token_seq'),
                                                     token VARCHAR(256) NOT NULL UNIQUE,
                                                     expiry TIMESTAMP WITH TIME ZONE NOT NULL,
                                                     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                                     status VARCHAR(20) NOT NULL,
                                                     account_id BIGINT NOT NULL,
                                                     email VARCHAR(255) NOT NULL,
                                                     type VARCHAR(20) NOT NULL,
                                                     CONSTRAINT fk_password_reset_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE INDEX IF NOT EXISTS idx_password_reset_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_account_id ON password_reset_tokens(account_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_email ON password_reset_tokens(email);
CREATE INDEX IF NOT EXISTS idx_password_reset_account_type_status_created ON password_reset_tokens(account_id, type, status, created_at);
