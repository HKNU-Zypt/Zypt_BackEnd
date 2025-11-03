CREATE INDEX idx_member_id_create_date ON focus_time (member_id, create_date);

CREATE INDEX idx_member_social_id_social_type ON member (social_type, social_id);
