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
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeInsertDto;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FocusTimeJpaRepository {

    @PersistenceContext
    private final EntityManager em;


    // focus time 영속성화
    @Transactional
    public Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime start_at, LocalTime end_at) {
        FocusTime newFocusTime
                = new FocusTime(member, start_at, end_at, date);

        em.persist(newFocusTime);

        return Optional.of(newFocusTime);
    }

}
