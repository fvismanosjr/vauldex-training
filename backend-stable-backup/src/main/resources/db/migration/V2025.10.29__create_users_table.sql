CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password TEXT NOT NULL CHECK (length(password) > 5),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL
);

CREATE INDEX users_username_idx ON users (username);
CREATE UNIQUE INDEX users_username_ukey ON users (username) WHERE deleted_at IS NULL;