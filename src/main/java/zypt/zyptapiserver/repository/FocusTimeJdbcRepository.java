package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.*;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FocusTimeJdbcRepository implements FocusTimeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime start_at, LocalTime end_at) {
        String sql = "INSERT INTO focus_time(member_id, start_at, end_at, create_date, focus_time, total_time) values(:member_id, :start_at, :end_at, :create_date, :focus_time, :total_time)";
        long totalTime = ChronoUnit.SECONDS.between(start_at, end_at);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("member_id", member.getId());
        param.addValue("start_at", start_at);
        param.addValue("end_at", end_at);
        param.addValue("create_date", date);
        param.addValue("focus_time", date);
        param.addValue("total_time", totalTime);

        int update = jdbcTemplate.update(sql, param, keyHolder);

        if (update == 0) {
            throw new IllegalArgumentException("focusTime 저장 실패");
        }

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException("focusTime 저장 성공했으나 생성된 ID를 가져오지 못했습니다.");
        }

        long id = keyHolder.getKey().longValue();
        return Optional.of(new FocusTime(id, member, start_at, end_at, date));
    }

    // 벌크연산으로 집중하지않은 시간들을 저장
    // 집중하지 않은 시간 총합을 반환
    @Transactional
    public Long bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes) {
        String sql = "INSERT INTO fragmented_unfocused_time(focused_id, start_at, end_at, type, unfocused_time) " +
                "VALUES(:focus_id,:start_at,:end_at,:type,:unfocused_time)";

        List<SqlParameterSource> batchParams = new ArrayList<>();

        for (FragmentedUnFocusedTimeInsertDto insertDto : unfocusedTimes) {
            MapSqlParameterSource param = new MapSqlParameterSource();
            param.addValue("focus_id", focusId);
            param.addValue("start_at", insertDto.startAt());
            param.addValue("end_at", insertDto.endAt());
            param.addValue("type", insertDto.type());
            param.addValue("unfocused_time", insertDto.calculateUnfocusedDuration());
            batchParams.add(param);
        }

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchParams.toArray(new SqlParameterSource[0]));

        int sum = Arrays.stream(updateCounts).sum();
        if (sum != unfocusedTimes.size()) {
            throw new IllegalArgumentException("Unfocus 저장 실패");
        }

        return unfocusedTimes.stream().mapToLong(FragmentedUnFocusedTimeInsertDto::calculateUnfocusedDuration).sum();
    }

    @Override
    public List<FocusTimeResponseDto> findAllFocusTimes(String memberId) {
        String sql = "SELECT * FROM focus_time WHERE member_id=:memberId ORDER BY create_date";

        return jdbcTemplate.query(sql, new MapSqlParameterSource("memberId", memberId), (rs, row)->
            new FocusTimeResponseDto(rs.getLong("id"),
                    rs.getString("member_id"),
                    rs.getTime("start_at").toLocalTime(),
                    rs.getTime("end_at").toLocalTime(),
                    rs.getDate("create_date").toLocalDate(),
                    null)
        );
    }

    @Override
    public List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIdList) {
        String sql = "SELECT * FROM fragmented_unfocused_time WHERE IN(:focusIdList)";

        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("focusIdList", focusIdList);

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) ->
                        new FragmentedUnFocusedTimeDto(
                                rs.getLong("id"),
                                rs.getLong("focus_id"),
                                rs.getTime("start_at").toLocalTime(),
                                rs.getTime("end_at").toLocalTime(),
                                UnFocusedType.valueOf(rs.getString("type")),
                                rs.getLong("unfocused_time")));
    }

    @Override
    public Optional<FocusTimeDto> findFocusTime(long focusId) {

        return Optional.empty();
    }

    @Override
    public List<FocusTimeDto> findFocusTimesByLocalDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<FocusDayMarkDto> findFocusTimesByMonth(int year, int month) {
        return List.of();
    }


}
