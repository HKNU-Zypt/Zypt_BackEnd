CREATE TABLE IF NOT EXISTS level_exp (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id varchar(36) NOT NULL,
    member_level int NOT NULL,
    cur_exp int NOT NULL,
    FOREIGN KEY(member_id) REFERENCES member (id) ON DELETE CASCADE
)

CREATE TABLE alarm (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    push_at TIME(0) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);