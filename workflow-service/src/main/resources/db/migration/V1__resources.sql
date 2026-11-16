CREATE TABLE resources (
    resource_type VARCHAR(80) NOT NULL,
    id VARCHAR(200) NOT NULL,
    payload TEXT NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    PRIMARY KEY (resource_type, id)
);
CREATE TABLE seed_history (seed_key VARCHAR(100) PRIMARY KEY);
