CREATE TABLE short_urls (
    id BIGSERIAL NOT NULL,
    original_url VARCHAR(2048) NOT NULL,
    short_code VARCHAR(32) NOT NULL,
    custom_alias VARCHAR(32) NULL,
    click_count BIGINT NOT NULL,
    enabled BOOLEAN NOT NULL,
    expires_at TIMESTAMP(6) NULL,
    created_by_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_short_urls_short_code UNIQUE (short_code),
    CONSTRAINT fk_short_urls_created_by FOREIGN KEY (created_by_id) REFERENCES users (id)
);

CREATE INDEX idx_short_urls_created_by_id ON short_urls (created_by_id);
CREATE INDEX idx_short_urls_expires_at ON short_urls (expires_at);
