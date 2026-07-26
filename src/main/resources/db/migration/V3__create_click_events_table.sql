CREATE TABLE click_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    short_url_id BIGINT NOT NULL,
    created_by_id BIGINT NOT NULL,
    ip_address VARCHAR(64) NOT NULL,
    country VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    browser VARCHAR(100) NULL,
    operating_system VARCHAR(100) NULL,
    device VARCHAR(100) NULL,
    referral VARCHAR(512) NULL,
    clicked_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_click_events_short_url FOREIGN KEY (short_url_id) REFERENCES short_urls (id),
    CONSTRAINT fk_click_events_created_by FOREIGN KEY (created_by_id) REFERENCES users (id)
);

CREATE INDEX idx_click_events_clicked_at ON click_events (clicked_at);
CREATE INDEX idx_click_events_short_url_id ON click_events (short_url_id);
CREATE INDEX idx_click_events_created_by_id ON click_events (created_by_id);
CREATE INDEX idx_click_events_country ON click_events (country);
CREATE INDEX idx_click_events_browser ON click_events (browser);
CREATE INDEX idx_click_events_device ON click_events (device);
