package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.FocusTimeInsertDto;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeInsertDto;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FocusTimeRepository {

    private final JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private final EntityManager em;


    // focus time 영속성화
    @Transactional
    public Optional<FocusTime> saveFocusTime(Member member, FocusTimeInsertDto focusTimeInsertDto) {
        FocusTime newFocusTime
                = new FocusTime(member, focusTimeInsertDto.startAt(), focusTimeInsertDto.endAt());

        em.persist(newFocusTime);

        return Optional.of(newFocusTime);
    }

    // 벌크연산으로 집중하지않은 시간들을 저장
    // 집중하지 않은 시간 총합을 반환
    @Transactional
    public Long bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes) {
        String sql = "INSERT INTO fragmented_unfocused_time(focused_id, start_at, end_at, type, unfocused_time) VALUES(?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                FragmentedUnFocusedTimeInsertDto unfocusedTime = unfocusedTimes.get(i);
                ps.setLong(1, focusId);
                ps.setTime(2, Time.valueOf(unfocusedTime.startAt()));
                ps.setTime(3, Time.valueOf(unfocusedTime.endAt()));
                ps.setString(4, unfocusedTime.type().name()); // enum 타입은 ps에선 식별 불가, String으로 넘김
                ps.setLong(5, unfocusedTime.calculateUnfocusedDuration());
            }

            @Override
            public int getBatchSize() {
                return unfocusedTimes.size();
            }
        });

        return unfocusedTimes.stream().mapToLong(FragmentedUnFocusedTimeInsertDto::calculateUnfocusedDuration).sum();
    }



}
