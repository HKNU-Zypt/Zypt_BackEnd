package zypt.zyptapiserver.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.SocialRefreshToken;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberJdbcRepository {


    private final NamedParameterJdbcTemplate template;

    @Transactional
    public Member save(Member member) {
        String sql = "INSERT INTO member(id, nick_name, email, social_type, social_id, create_at, last_modified_at) values(:id, :nick_name, :email, :social_type, :social_id, :create_at, :last_modified_at)";

        KeyHolder keyHolder = new GeneratedKeyHolder();


        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", member.getId());
        param.addValue("nick_name", member.getNickName());
        param.addValue("email", member.getEmail());
        param.addValue("social_type", member.getSocialType().name());
        param.addValue("social_id", member.getSocialId());
        param.addValue("create_at", LocalDateTime.now());
        param.addValue("last_modified_at", LocalDateTime.now());

        int update = template.update(sql, param, keyHolder);

        return member;
    }

}
