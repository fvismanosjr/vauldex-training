DROP TABLE IF EXISTS chats CASCADE;
DROP TABLE IF EXISTS chat_members CASCADE;
DROP TABLE IF EXISTS chat_messages CASCADE;

CREATE TABLE chats (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) DEFAULT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL
);

CREATE INDEX chats_name_idx ON chats (name);

CREATE TABLE chat_members (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at DATE DEFAULT NULL,
    left_at DATE DEFAULT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL,
    CONSTRAINT chat_members_chat_fkey FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chat_members_user_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE chat_messages_types (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL
);

CREATE INDEX chat_messages_types_name_idx ON chat_messages_types (name);

INSERT INTO chat_messages_types(name) VALUES('text');
INSERT INTO chat_messages_types(name) VALUES('file');

CREATE TABLE chat_messages_statuses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL
);

CREATE INDEX chat_messages_statuses_name_idx ON chat_messages_statuses (name);

INSERT INTO chat_messages_statuses(name) VALUES('sent');
INSERT INTO chat_messages_statuses(name) VALUES('delivered');
INSERT INTO chat_messages_statuses(name) VALUES('read');

CREATE TABLE chat_messages (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    body TEXT DEFAULT NULL,
    chat_message_type_id BIGINT NOT NULL,
    chat_message_status_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL,
    CONSTRAINT chat_messages_chat_fkey FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chat_messages_user_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chat_messages_chat_messages_type_fkey FOREIGN KEY (chat_message_type_id) REFERENCES chat_messages_types(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chat_messages_chat_messages_status_fkey FOREIGN KEY (chat_message_status_id) REFERENCES chat_messages_statuses(id) ON DELETE CASCADE ON UPDATE CASCADE
);