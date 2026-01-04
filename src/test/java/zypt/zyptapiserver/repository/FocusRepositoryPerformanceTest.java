package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.dto.focustime.FocusTimeResponseDto;
import zypt.zyptapiserver.repository.Member.MemberJdbcRepository;
import zypt.zyptapiserver.repository.Member.MemberRepositoryImpl;
import zypt.zyptapiserver.repository.focustime.FocusTimeJdbcRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeJpaRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeMyBatisRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class FocusRepositoryPerformanceTest {

    @Autowired
    FocusTimeJpaRepository jpaRepository;

    @Autowired
    FocusTimeJdbcRepository jdbcRepository;

    @Autowired
    FocusTimeMyBatisRepository myBatisRepository;

    @Autowired
    MemberRepositoryImpl memberJpaRepository;
    @Autowired
    private MemberJdbcRepository memberJdbcRepository;

    @Autowired
    EntityManager em;

    Member member;


    @BeforeEach
    void init() {
        member = memberJpaRepository.save(Member.builder().email("abc@naver.com").nickName(UUID.randomUUID().toString()).build());
        em.flush();
    }

    @Test
    @Transactional
    @DisplayName("jpa insert 성능 테스트")
    void insertFocusJPATest() {

        double jpaAVG = getJpaAvg(jpaRepository, member,0);


        log.info("jpa = {} ms", jpaAVG);
    }


    @Test
    @Transactional
    @DisplayName("jdbc insert 성능 테스트")
    void insertFocusJDBCTest() {

        double jdbcAVG = getAvg(jdbcRepository, member,0);

        log.info("jdbc = {} ms", jdbcAVG);
    }

    @Test
    @Transactional
    @DisplayName("mybais insert 성능 테스트")
    void insertFocusMybatisTest() {
        double mybatisAVG = getAvg(myBatisRepository, member,0);

        log.info("mybatis = {} ms", mybatisAVG);
    }

    private double getAvg(FocusTimeRepository repository, Member member, long avg) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        // 예열
        for (int j = 0; j < 1000; j++) {
            repository.saveFocusTime(member, date, time, time, 1L);
        }

        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();

            for (int j = 0; j < 10000; j++) {
                repository.saveFocusTime(member, date.plusDays(j), time.plusSeconds(j), time.plusSeconds(j + 1), 1L);

            }
            long end = System.currentTimeMillis();

            avg += end - start;
        }
        return avg / 5.0;
    }

    private double getJpaAvg(FocusTimeRepository repository, Member member, long avg) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        // 예열
        for (int j = 0; j < 1000; j++) {
            repository.saveFocusTime(member, date, time.minusMinutes(j), time.plusMinutes(j), 1L);
        }

        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            for (int j = 0; j < 10000; j++) {
                repository.saveFocusTime(member, date, time.minusMinutes(j), time.plusMinutes(j), 1L);

            }
            long end = System.currentTimeMillis();

            avg += end - start;
        }
        return avg / 5.0;
    }

    @Test
    @DisplayName("조회 성능")
    void find() {

        log.info("jpa select = {} ms", findAll(jpaRepository));
        log.info("jdbc select = {} ms", findAll(jdbcRepository));
        log.info("myBatis select = {} ms", findAll(myBatisRepository));
    }

    private double findAll(FocusTimeRepository repository) {
        // 예열
        List<FocusTimeResponseDto> a =
                repository.findFocusTimesByYearAndMonthAndDay("test", 2025, 7, 25);

        long avg = 0;
        for (int i = 0; i < 5; i++) {
            em.clear();
            long start = System.currentTimeMillis();
            List<FocusTimeResponseDto> dto =
                    repository.findFocusTimesByYearAndMonthAndDay("test", 2025, 7, null);
            long end = System.currentTimeMillis();

            avg += end - start;
        }

        return avg / 5.0;
    }


}
