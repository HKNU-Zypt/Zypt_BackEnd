package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class FocusRepositoryPerformanceTest {

    @Autowired
    FocusTimeJpaRepository jpaRepository;

    @Autowired
    FocusTimeJdbcRepository jdbcRepository;

    @Autowired
    FocusTimeMyBatisRepository myBatisRepository;

    @Autowired
    MemberRepository memberJpaRepository;
    @Autowired
    private MemberJdbcRepository memberJdbcRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("insert 성능 테스트")
    void insertFocusJPATest() {
        Member member = memberJpaRepository.save(Member.builder().email("abc@naver.com").socialType(SocialType.KAKAO).socialId("123").nickName(UUID.randomUUID().toString()).build());
        double jpaAVG = getJpaAvg(jpaRepository, member,0);

        log.info("jpa = {} ms", jpaAVG);
    }


    @Test
    @DisplayName("insert 성능 테스트")
    void insertFocusJDBCTest() {
        Member commitedMember = memberJdbcRepository.save(new Member(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "abc@google.com", SocialType.GOOGLE, "abc" ));
        double jdbcAVG = getAvg(jdbcRepository, commitedMember,0);

        log.info("jdbc = {} ms", jdbcAVG);
    }

    @Test
    @DisplayName("insert 성능 테스트")
    void insertFocusMybatisTest() {
        Member commitedMember = memberJdbcRepository.save(new Member(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "abc@google.com", SocialType.GOOGLE, "abc" ));
        double mybatisAVG = getAvg(myBatisRepository, commitedMember,0);

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
            for (int j = 0; j < 1000; j++) {
                repository.saveFocusTime(member, date, time, time, 1L);

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
            repository.saveFocusTime(member, date, time, time, 1L);
        }

        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            for (int j = 0; j < 1000; j++) {
                repository.saveFocusTime(member, date, time, time, 1L);

            }
            long end = System.currentTimeMillis();

            avg += end - start;
        }
        return avg / 5.0;
    }

    @Test
    @DisplayName("집중 시간 전부 조회 성능 테스트")
    void findAllTest() {

    }
}
