CREATE TABLE error_report (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    member_id VARCHAR(50) NOT NULL,
    req_id VARCHAR(50) NOT NULL,
    report_date datetime(6) NOT NULL,
    body TEXT
);

CREATE INDEX idx_error_report_date ON error_report (report_date);