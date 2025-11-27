DROP TABLE IF EXISTS degrees CASCADE;
DROP TABLE IF EXISTS students CASCADE;

CREATE TABLE degrees (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(60) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL
);

CREATE INDEX degrees_name_idx ON degrees (name);

CREATE TABLE students (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    age SMALLINT CHECK (age >= 1) NOT NULL,
    degree_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    last_modified_by VARCHAR(100) DEFAULT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    deleted_by VARCHAR(100) DEFAULT NULL,
    CONSTRAINT students_degree_fkey FOREIGN KEY (degree_id) REFERENCES degrees(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX students_name_idx ON students (name);
CREATE INDEX students_email_idx ON students (email);
CREATE INDEX students_age_idx ON students (age);
CREATE INDEX students_name_email_age_idx ON students (name, email, age);
CREATE UNIQUE INDEX students_email_ukey ON students (email) WHERE deleted_at IS NULL;

INSERT INTO degrees (name, abbreviation) VALUES ('Bachelor of Science in Information Technology', 'BSIT');
INSERT INTO degrees (name, abbreviation) VALUES ('Bachelor of Science in Computer Studies', 'BSCS');
INSERT INTO degrees (name, abbreviation) VALUES ('Bachelor of Science in Information System', 'BSIS');