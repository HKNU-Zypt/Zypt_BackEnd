package zypt.zyptapiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.FocusTimeDto;
import zypt.zyptapiserver.domain.dto.FocusTimeResponseDto;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    MemberRepository memberRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void init() {

        log.info("초기화 ");
        Member member = Member.builder().email("abc@aa.cc").nickName(UUID.randomUUID().toString()).socialType(SocialType.KAKAO).socialId("2gjdkl12333").build();
        memberRepository.save(member);

        List<Long> list = new ArrayList<>();
        log.info("중간 초기화 ");

        FocusTime focusTime = jpaRepository.saveFocusTime(member, LocalDate.of(2025, 6, 30), LocalTime.now(), LocalTime.now().plusHours(1)).get();
        FocusTime focusTime1 = jpaRepository.saveFocusTime(member, LocalDate.of(2025, 6, 29), LocalTime.now(), LocalTime.now().plusHours(1)).get();
        FocusTime focusTime2 = jpaRepository.saveFocusTime(member, LocalDate.of(2025, 5, 30), LocalTime.now(), LocalTime.now().plusHours(1)).get();
        FocusTime focusTime3 = jpaRepository.saveFocusTime(member, LocalDate.of(2024, 5, 29), LocalTime.now(), LocalTime.now().plusHours(1)).get();
        FocusTime focusTime4 = jpaRepository.saveFocusTime(member, LocalDate.of(2024, 5, 28), LocalTime.now(), LocalTime.now().plusHours(1)).get();
        log.info("초기화 성공");


    }


    @AfterEach
    void delete() {
        Member member = memberRepository.findBySocialId(SocialType.KAKAO, "2gjdkl12333").get();
        log.info("해당 id 삭제 ={}", member.getId());
        log.info("영속화 ? = {}", em.contains(member));
        memberRepository.deleteMember(member);
        transactionTemplate.execute(status -> {

            return 1;
        });
    }

    @Test
    @Transactional
    @DisplayName("집중 데이터 멤버 ID로 전부 조회 성공 테스트")
    void findAllFocusTime() {

        Member member = memberRepository.findBySocialId(SocialType.KAKAO, "2gjdkl12333").get();
        Member member1 = memberRepository.findMemberById(member.getId()).get();
        log.info("1. 영속화 ? ={}", em.contains(member));
        log.info("2. 영속화 ? ={}", em.contains(member1));

        // given when
        List<FocusTimeResponseDto> allFocusTimes = repository.findAllFocusTimes(member.getId());

        //then
        Assertions.assertThat(allFocusTimes.size()).isEqualTo(5);

    }

    @Test
    @DisplayName("집중 데이터 조회 특정 날짜 테스트")
    void findFocusTime() {

        Member member = memberRepository.findBySocialId(SocialType.KAKAO, "2gjdkl12333").get();
        // given when
        List<FocusTimeResponseDto> focusByYear = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), 2025, null, null);
        List<FocusTimeResponseDto> focusByYearAndMonth = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), 2025, 6, null);
        List<FocusTimeResponseDto> focusByYearAndMonthAndDay = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), 2024, 5, 28);
        List<FocusTimeResponseDto> focus = repository.findFocusTimesByYearAndMonthAndDay(member.getId(), null, null, null);

        //then
        Assertions.assertThat(focusByYear.size()).isEqualTo(3);
        Assertions.assertThat(focusByYearAndMonth.size()).isEqualTo(2);
        Assertions.assertThat(focusByYearAndMonthAndDay.size()).isEqualTo(1);
        Assertions.assertThat(focus.size()).isEqualTo(5);

    }

    @Test
    @DisplayName("집중 데이터 삭제 특정 날짜 테스트")
    void deleteByDate() {

        // given
        Member member = memberRepository.findBySocialId(SocialType.KAKAO, "2gjdkl12333").get();

        //when
//        repository.deleteFocusTimeByYearAndMonthAndDay(member.getId(), 2025, null, null);
        repository.deleteFocusTimeByYearAndMonthAndDay(member.getId(), 2025, 5, 30);
        List<FocusTimeResponseDto> allFocusTimes = repository.findAllFocusTimes(member.getId());


        //then
//        Assertions.assertThat(allFocusTimes.size()).isEqualTo(2);
        Assertions.assertThat(allFocusTimes.size()).isEqualTo(4);

    }

}