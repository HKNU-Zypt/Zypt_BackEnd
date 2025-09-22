CREATE TABLE IF NOT EXISTS member (
    id VARCHAR(36)  PRIMARY KEY ,
    nick_name VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(50) NOT NULL,
    social_type ENUM('KAKAO', 'GOOGLE', 'NAVER') NOT NULL,
    social_id VARCHAR(50) NOT NULL,
    create_at DATETIME(3) NOT NULL,
    last_modified_at DATETIME(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS focus_time (
    id BIGINT primary key AUTO_INCREMENT,
    member_id VARCHAR(36) NOT NULL,
    start_at TIME(0) NOT NULL,
    end_at TIME(0) NOT NULL,
    create_date DATE NOT NULL,
    focus_time BIGINT,
    total_time BIGINT,

    FOREIGN KEY (member_id) REFERENCES member(id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE(create_date, start_at, end_at)
);


CREATE TABLE IF NOT EXISTS fragmented_unfocused_time (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    focus_id BIGINT,
    start_at TIME(0) NOT NULL,
    end_at TIME(0) NOT NULL,
    type ENUM('SLEEP', 'DISTRACTED') NOT NULL,
    unfocused_time BIGINT NOT NULL,

    FOREIGN KEY (focus_id) REFERENCES focus_time(id) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS social_refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id varchar(36),
    token varchar(255),
    social_type ENUM('GOOGLE', 'NAVER'),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);
