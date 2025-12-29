-- liquibase formatted sql

-- changeset antigravity:008-create-google-tokens-table
CREATE TABLE google_tokens (
    store_id VARCHAR(255) NOT NULL,
    key_id VARCHAR(255) NOT NULL,
    token_data BYTEA NOT NULL,
    PRIMARY KEY (store_id, key_id)
);
