CREATE EXTENSION IF NOT EXISTS pgcrypto;

DROP TABLE IF EXISTS degrees CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    created_by VARCHAR(100),
    last_modified_at TIMESTAMPTZ,
    last_modified_by VARCHAR(100),
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100)
);

CREATE INDEX roles_name_idx ON roles (name);

INSERT INTO roles (name) VALUES
('ROLE_SUDO'),
('ROLE_ADMIN'),
('ROLE_STUDENT');

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password TEXT NOT NULL CHECK (LENGTH(password) > 5),
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    created_by VARCHAR(100),
    last_modified_at TIMESTAMPTZ,
    last_modified_by VARCHAR(100),
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100)
);

CREATE INDEX users_username_idx ON users (username);
CREATE UNIQUE INDEX users_username_ukey ON users (username) WHERE deleted_at IS NULL;

INSERT INTO users (username, password, role_id) VALUES
('superadmin', '$2a$12$iK9pwpXbdcfG7kL7yrKrEepsJLcGrlKqZs6aXoh7vg253jxG4R86m', 1);

CREATE TABLE degrees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(60) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    created_by VARCHAR(100),
    last_modified_at TIMESTAMPTZ,
    last_modified_by VARCHAR(100),
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100)
);

CREATE INDEX degrees_name_idx ON degrees (name);
CREATE INDEX degrees_abbreviation_idx ON degrees (abbreviation);

CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    birthdate DATE NOT NULL,
    degree_id BIGINT REFERENCES degrees(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    created_by VARCHAR(100),
    last_modified_at TIMESTAMPTZ,
    last_modified_by VARCHAR(100),
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100)
);

CREATE INDEX students_name_idx ON students (name);
CREATE INDEX students_email_idx ON students (email);
CREATE INDEX students_name_email_idx ON students (name, email);
CREATE UNIQUE INDEX students_email_ukey ON students (email) WHERE deleted_at IS NULL;

INSERT INTO degrees (name, abbreviation) VALUES
('Bachelor of Science in Information Technology', 'BSIT'),
('Bachelor of Science in Computer Studies', 'BSCS'),
('Bachelor of Science in Information System', 'BSIS');
