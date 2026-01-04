package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.dto.focustime.FocusTimeResponseDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.repository.Member.MemberRepositoryImpl;
import zypt.zyptapiserver.repository.focustime.FocusTimeJdbcRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeJpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class FocusTimeJdbcRepositoryTest {

    @Autowired
    FocusTimeJdbcRepository repository;

    @Autowired
    FocusTimeJpaRepository jpaRepository;

    @Autowired
    MemberRepositoryImpl memberRepositoryImpl;

    @Autowired
    TransactionTemplate transactionTemplate;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void init() {

        log.info("초기화 ");
        Member member = Member.builder().email("abc@aa.cc").nickName(UUID.randomUUID().toString()).build();
        memberRepositoryImpl.save(member);

        log.info("member 영속화 ? = {}", em.contains(member));
        log.info("중간 초기화 ");

        jpaRepository.saveFocusTime(member, LocalDate.of(2025, 6, 30), LocalTime.now(), LocalTime.now().plusHours(1), 1L).get();
        jpaRepository.saveFocusTime(member, LocalDate.of(2025, 6, 29), LocalTime.now(), LocalTime.now().plusHours(1), 1L).get();
        jpaRepository.saveFocusTime(member, LocalDate.of(2025, 5, 30), LocalTime.now(), LocalTime.now().plusHours(1), 1L).get();
        jpaRepository.saveFocusTime(member, LocalDate.of(2024, 5, 29), LocalTime.now(), LocalTime.now().plusHours(1), 1L).get();
        FocusTime focusTime = jpaRepository.saveFocusTime(member, LocalDate.of(2024, 5, 28), LocalTime.now(), LocalTime.now().plusHours(1), 1L).get();

        log.info("초기화 성공");

    }

    @Test
    @Transactional
    @DisplayName("집중 데이터 멤버 ID로 전부 조회 성공 테스트")
    void findAllFocusTime() {

        Member member = memberRepositoryImpl.findBySocialId(SocialType.KAKAO, "2gjdkl12333").get();

        // given when
        List<FocusTimeResponseDto> allFocusTimes = repository.findAllFocusTimes(member.getId());

        //then
        Assertions.assertThat(allFocusTimes.size()).isEqualTo(5);

    }

    @Test
    @Transactional
    @DisplayName("집중 데이터 조회 특정 날짜 테스트")
    void findFocusTime() {

        Member member = memberRepositoryImpl.findBySocialId(SocialType.KAKAO, "2gjdkl12333").get();
        // given when
        List<FocusTimeResponseDto> focusByYear = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), 2025, null, null);
        List<FocusTimeResponseDto> focusByYearAndMonth = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), 2025, 6, null);
        List<FocusTimeResponseDto> focusByYearAndMonthAndDay = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), 2024, 5, 28);
        List<FocusTimeResponseDto> focus = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), null, null, null);
        for (FocusTimeResponseDto allFocusTime : focusByYear) {
            log.info("ft = {}", allFocusTime.getCreateDate());
        }
        //then
        Assertions.assertThat(focusByYear.size()).isEqualTo(3);
        Assertions.assertThat(focusByYearAndMonth.size()).isEqualTo(2);
        Assertions.assertThat(focusByYearAndMonthAndDay.size()).isEqualTo(1);
        Assertions.assertThat(focus.size()).isEqualTo(5);

    }

    @Test
    @Transactional
    @DisplayName("집중 데이터 삭제 특정 날짜 테스트")
    void deleteByDate() {

        // given
        Member member = memberRepositoryImpl.findBySocialId(SocialType.KAKAO, "2gjdkl12333").get();

        //when
//        repository.deleteFocusTimeByYearAndMonthAndDay(member.getId(), 2025, null, null);
        List<Long> ids = repository.findFocusTimeIdsByDate(member.getId(), 2025, 5, 30);
        repository.deleteFocusTimeByYearAndMonthAndDay(member.getId(), 2025, 5, 30, ids);
        List<FocusTimeResponseDto> allFocusTimes = repository.findAllFocusTimes(member.getId());


        //then
//        Assertions.assertThat(allFocusTimes.size()).isEqualTo(2);
        Assertions.assertThat(allFocusTimes.size()).isEqualTo(4);

    }

}