package zypt.zyptapiserver.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.Service.MemberServiceImpl;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.*;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FocusTimeJdbcRepository implements FocusTimeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final MemberServiceImpl memberService;

    @Override
    public Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime startAt, LocalTime endAt, Long sumUnFocusedTimes) {
        String sql = "INSERT INTO focus_time(member_id, start_at, end_at, create_date, focus_time, total_time) values(:member_id, :start_at, :end_at, :create_date, :focus_time, :total_time)";
        long totalTime = ChronoUnit.SECONDS.between(startAt, endAt);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("member_id", member.getId());
        param.addValue("start_at", startAt);
        param.addValue("end_at", endAt);
        param.addValue("create_date", date);
        param.addValue("focus_time", totalTime - sumUnFocusedTimes);
        param.addValue("total_time", totalTime);

        int update = jdbcTemplate.update(sql, param, keyHolder);

        if (update == 0) {
            throw new IllegalStateException("focusTime 저장 실패");
        }

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException("focusTime 저장 성공했으나 생성된 ID를 가져오지 못했습니다.");
        }

        long id = keyHolder.getKey().longValue();
        return Optional.of(new FocusTime(id, member, startAt, endAt, date));
    }

    // 벌크연산으로 집중하지않은 시간들을 저장
    // 집중하지 않은 시간 총합을 반환
    @Transactional
    public void bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes) {
        String sql = "INSERT INTO fragmented_unfocused_time(focus_id, start_at, end_at, type, unfocused_time) " +
                "VALUES(:focus_id,:start_at,:end_at,:type,:unfocused_time)";

        List<SqlParameterSource> batchParams = new ArrayList<>();

        for (FragmentedUnFocusedTimeInsertDto insertDto : unfocusedTimes) {
            MapSqlParameterSource param = new MapSqlParameterSource();
            param.addValue("focus_id", focusId);
            param.addValue("start_at", insertDto.startAt());
            param.addValue("end_at", insertDto.endAt());
            param.addValue("type", insertDto.type().name());
            param.addValue("unfocused_time", insertDto.calculateUnfocusedDuration());
            batchParams.add(param);
        }

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchParams.toArray(new SqlParameterSource[0]));

        int sum = Arrays.stream(updateCounts).sum();
        if (sum != unfocusedTimes.size()) {
            throw new IllegalArgumentException("Unfocus 저장 실패");
        }
    }

    @Override
    public List<FocusTimeResponseDto> findAllFocusTimes(String memberId) {
        String sql = "SELECT * FROM focus_time WHERE member_id=:memberId ORDER BY create_date";

        return jdbcTemplate.query(sql, new MapSqlParameterSource("memberId", memberId), (rs, row)->
            new FocusTimeResponseDto(rs.getLong("id"),
                    rs.getString("member_id"),
                    rs.getTime("start_at").toLocalTime(),
                    rs.getTime("end_at").toLocalTime(),
                    rs.getDate("create_date").toLocalDate())
        );
    }

    @Override
    public List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIdList) {
        String sql = "SELECT * FROM fragmented_unfocused_time WHERE focus_id IN(:focusIdList)";


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
    public Optional<FocusTimeResponseDto> findFocusTime(long focusId) {
        String sql = "SELECT * FROM focus_time WHERE id = :focusId";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("focusId", focusId);

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) ->
                        new FocusTimeResponseDto(
                                rs.getLong("id"),
                                rs.getString("member_id"),
                                rs.getTime("start_at").toLocalTime(),
                                rs.getTime("end_at").toLocalTime(),
                                rs.getDate("create_date").toLocalDate())
        ).stream().findFirst();
    }


//    @Override
//    public List<FocusTimeDto> findFocusTimesByYearAndMonthAndDay(int year, int month, int day) {
//        LocalDate findDate = LocalDate.of(year, month, day);
//        String sql = "SELECT * FROM focus_time WHERE create_date =:findDate";
//        MapSqlParameterSource param = new MapSqlParameterSource();
//        param.addValue("findDate", findDate);
//
//        return jdbcTemplate.query(sql, param,
//                (rs, rowNum) ->
//                        new FocusTimeDto(
//                                rs.getString("member_id"),
//                                rs.getTime("start_at").toLocalTime(),
//                                rs.getTime("end_at").toLocalTime(),
//                                rs.getDate("create_date").toLocalDate()
//                                , null
//                        ));
//    }
//
    @Override
    public List<FocusTimeResponseDto> findFocusTimesByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day) {
        String sql = "SELECT * FROM focus_time WHERE member_id = :memberId";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("memberId", memberId);

        if (year != null) {
            sql += " AND YEAR(create_date) =:_year";
            param.addValue("_year", year);
        }

        if (month != null) {
            sql += " AND MONTH(create_date) =:_month";
            param.addValue("_month", month);
        }

        if (day != null) {
            sql += " AND DAY(create_date) =:_day";
            param.addValue("_day", day);
        }

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) ->
                        new FocusTimeResponseDto(
                                rs.getLong("id"),
                                rs.getString("member_id"),
                                rs.getTime("start_at").toLocalTime(),
                                rs.getTime("end_at").toLocalTime(),
                                rs.getDate("create_date").toLocalDate()
                        ));
    }
//
//    @Override
//    public List<FocusTimeDto> findFocusTimesByYearAndMonth(int year, int month) {
//        String sql = "SELECT * FROM focus_time WHERE YEAR(create_date) = :year AND MONTH(create_date) = :month";
//        MapSqlParameterSource param = new MapSqlParameterSource();
//        param.addValue("year", year);
//        param.addValue("month", month);
//
//        return jdbcTemplate.query(sql, param,
//                (rs, rowNum) ->
//                        new FocusTimeDto(
//                                rs.getString("member_id"),
//                                rs.getTime("start_at").toLocalTime(),
//                                rs.getTime("end_at").toLocalTime(),
//                                rs.getDate("create_date").toLocalDate()
//                                , null
//                        ));
//    }
//
//    @Override
//    public List<FocusTimeDto> findFocusTimesByYear(int year) {
//        String sql = "SELECT * FROM focus_time WHERE YEAR(create_date) = :year";
//        MapSqlParameterSource param = new MapSqlParameterSource();
//        param.addValue("year", year);
//
//        return jdbcTemplate.query(sql, param,
//                (rs, rowNum) ->
//                        new FocusTimeDto(
//                                rs.getString("member_id"),
//                                rs.getTime("start_at").toLocalTime(),
//                                rs.getTime("end_at").toLocalTime(),
//                                rs.getDate("create_date").toLocalDate()
//                                , null
//                        ));
//    }

    @Override
    public List<Integer> findFocusTimesByMonth(String memberId, int year, int month) {
        String sql = "SELECT DAY(create_date) as days FROM focus_time WHERE member_id = :memberId AND YEAR(create_date) =:year AND MONTH(create_date) = :month";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("memberId", memberId);
        param.addValue("year", year);
        param.addValue("month", month);

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) ->
                        rs.getInt("days")
        );
    }

    @Override
    @Transactional
    public void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day, List<Long> ids) {
        String deleteChildSql = "DELETE FROM fragmented_unfocused_time WHERE focus_id IN (:ids)";

        jdbcTemplate.update(deleteChildSql, new MapSqlParameterSource("ids", ids));

        String deleteParentSql = "DELETE FROM focus_time WHERE id IN (:ids)";
        int deleted = jdbcTemplate.update(deleteParentSql, new MapSqlParameterSource("ids", ids));

        if (deleted == 0) {
            throw new IllegalStateException("focusTime 삭제 실패");
        }
    }

    @Override
    public List<Long> findFocusTimeIdsByDate(String memberId, Integer year, Integer month, Integer day) {
        String sql = "SELECT id FROM focus_time WHERE member_id = :memberId";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("memberId", memberId);

        if (year != null) {
            sql += " AND YEAR(create_date) = :_year";
            param.addValue("_year", year);
        }
        if (month != null) {
            sql += " AND MONTH(create_date) = :_month";
            param.addValue("_month", month);
        }
        if (day != null) {
            sql += " AND DAY(create_date) = :_day";
            param.addValue("_day", day);
        }

        List<Long> ids = jdbcTemplate.query(sql, param, (rs, rowNum) -> rs.getLong("id"));
        return ids;
    }
}
