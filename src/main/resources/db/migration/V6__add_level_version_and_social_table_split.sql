ALTER TABLE level_exp
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE social_auth (
    id bigint primary key auto_increment,
    member_id varchar(36) not null,
    provider varchar(50) not null,
    provider_id VARCHAR(100) not null,
    create_at DATETIME(3) NOT NULL,
    last_modified_at DATETIME(3) NOT NULL,

    CONSTRAINT uk_provider_id UNIQUE (provider, provider_id),
    foreign key (member_id) references member(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_social_auth_login ON social_auth (provider, provider_id, member_id);

insert into social_auth(member_id, provider, provider_id, create_at, last_modified_at)
SELECT id, social_type, social_id, create_at, last_modified_at
FROM member;