DROP TABLE IF EXISTS member;

CREATE TABLE member (
    id VARCHAR(36)  PRIMARY KEY ,
    nickname VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(50) NOT NULL,
    social_type ENUM('KAKAO', 'GOOGLE', 'NAVER') NOT NULL,
    social_id VARCHAR(50) NOT NULL,
    create_at DATETIME(3) NOT NULL,
    last_modified_at DATETIME(3) NOT NULL,
    
);

CREATE INDEX idx_member_social_id_social_type ON member (social_type, social_id);




CREATE TABLE focus_time (
    id BIGINT primary key AUTO_INCREMENT,
    member_id VARCHAR(36) NOT NULL,
    start_at DATETIME(3) NOT NULL,
    end_at DATETIME(3) NOT NULL,
    focus_time BIGINT,
    total_time BIGINT,

    FOREIGN KEY (member_id) REFERENCES member(id) ON UPDATE CASCADE ON DELETE CASCADE
);

DROP TABLE IF EXISTS fragmented_unfocused_time;

CREATE TABLE fragmented_unfocused_time (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    focus_id BIGINT,
    start_at TIME NOT NULL,
    end_at TIME NOT NULL,
    type ENUM('SLEEP', 'DISTRACTED') NOT NULL,
    unfocused_time BIGINT NOT NULL,

    FOREIGN KEY (focus_id) REFERENCES focus_time(id) ON UPDATE CASCADE ON DELETE CASCADE
);