package zypt.zyptapiserver.Service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.FocusTimeDto;
import zypt.zyptapiserver.domain.dto.FocusTimeResponseDto;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeInsertDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.domain.enums.UnFocusedType;
import zypt.zyptapiserver.repository.MemberRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class FocusTimeServiceTest {

    @Autowired
    FocusTimeService service;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TransactionTemplate template;

    List<Member> members = new ArrayList<>();

    @BeforeEach
    void init() {
        for (int i = 0; i < 5; i++) {
            Member member = memberRepository.save(Member.builder().email("abc@gmail.com").socialId("abc" + i).socialType(SocialType.GOOGLE).nickName(UUID.randomUUID().toString()).build());
            members.add(member);
        }

        for (int i = 0; i < 5; i++) {
            List<FragmentedUnFocusedTimeInsertDto> dto = new ArrayList<>();
            Member member = members.get(i);

            for (int j = 0; j < 10; j++) {
                FragmentedUnFocusedTimeInsertDto insertDto = new FragmentedUnFocusedTimeInsertDto(LocalTime.now(), LocalTime.now().plus(Duration.ofMinutes(i)), UnFocusedType.SLEEP);
                dto.add(insertDto);
            }

            FocusTimeDto focusTimeDto = new FocusTimeDto(member.getId(), LocalTime.now().plus(Duration.ofMinutes(i)), LocalTime.now().plus(Duration.ofHours(2)), LocalDate.now().plusDays(i), dto);
            FocusTimeDto focusTimeDto1 = new FocusTimeDto(member.getId(), LocalTime.now().plus(Duration.ofMinutes(i)), LocalTime.now().plus(Duration.ofHours(2)), LocalDate.now().plusDays(i + 1), dto);
            FocusTimeDto focusTimeDto2 = new FocusTimeDto(member.getId(), LocalTime.now().plus(Duration.ofMinutes(i)), LocalTime.now().plus(Duration.ofHours(2)), LocalDate.now().plusDays(i + 2), dto);

            service.saveFocusTime(member.getId(), focusTimeDto);
            service.saveFocusTime(member.getId(), focusTimeDto1);
            service.saveFocusTime(member.getId(), focusTimeDto2);
        }
    }

    @Test
    @Transactional
    void saveMemberTest() {

        for (int i = 0; i < 5; i++) {
            List<FragmentedUnFocusedTimeInsertDto> dto = new ArrayList<>();
            Member member = members.get(i);

            for (int j = 0; j < 10; j++) {
                FragmentedUnFocusedTimeInsertDto insertDto = new FragmentedUnFocusedTimeInsertDto(LocalTime.now(), LocalTime.now().plus(Duration.ofMinutes(i)), UnFocusedType.SLEEP);
                dto.add(insertDto);
            }

            FocusTimeDto focusTimeDto = new FocusTimeDto(member.getId(), LocalTime.now().plus(Duration.ofMinutes(i)), LocalTime.now().plus(Duration.ofHours(2)), LocalDate.now(), dto);

            service.saveFocusTime(member.getId(), focusTimeDto);
        }
    }

    @Test
    @DisplayName("한 멤버의 모든 집중 시간을 조회해서 성공하는지 테스트")
    void findAllFocusTimeSuccessTest() {
        //given, when
        List<FocusTimeResponseDto> allFocusTimes = service.findAllFocusTimes(members.get(0).getId());

        // then
        Assertions.assertThat(allFocusTimes.size()).isEqualTo(3);
        Assertions.assertThat(allFocusTimes.get(0).getUnFocusedTimeDtos().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("없는 멤버 id로 실패 테스트")
    void findAllFocusTimeFailIdInvalidTest() {
        // then
        Assertions.assertThatThrownBy(() -> service.findAllFocusTimes("없는 id")).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    @DisplayName("조건에 맞는 focus 데이터가 없어 예외를 던지는 테스트")
    void findAllFocusTimeFailTest() {
        // NoSuchElementException
        // then
        Assertions.assertThatThrownBy(() -> service.findFocusTime(55555555)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("조건에 맞는 focus 데이터 하나 조회 테스트")
    void findOneFocusTimeTest() {
        // given when
        FocusTimeResponseDto focusTime = service.findFocusTime(1);
        // then
        Assertions.assertThat(focusTime).isNotNull();
    }

    @Test
    @DisplayName("날자 기반 조회 성공 테스트")
    void findOneFocusTimeByDateSuccessTest() {
        // given
        Member member = members.get(0);

        //when
        List<FocusTimeResponseDto> focusTimesByYear = service.findFocusTimesByYearAndMonthAndDay(member.getId(), 2025, null, null);
        List<FocusTimeResponseDto> focusTimesByYearAndMonth = service.findFocusTimesByYearAndMonthAndDay(member.getId(), 2025, 7, null);
        List<FocusTimeResponseDto> focusTimesByYearAndMonthAndDay = service.findFocusTimesByYearAndMonthAndDay(member.getId(), 2025, 7, 4);

        // then
        Assertions.assertThat(focusTimesByYear.size()).isEqualTo(3);
        Assertions.assertThat(focusTimesByYearAndMonth.size()).isEqualTo(3);
        Assertions.assertThat(focusTimesByYearAndMonthAndDay.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("날자 기반 조회 순서 틀림 실패 테스트")
    void findOneFocusTimeByDateFailTest() {
        // given
        Member member = members.get(0);

        //when
        Assertions.assertThatThrownBy(() -> service.findFocusTimesByYearAndMonthAndDay(member.getId(), null, 7, null)).isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> service.findFocusTimesByYearAndMonthAndDay(member.getId(), null, null, 4)).isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> service.findFocusTimesByYearAndMonthAndDay(member.getId(), null, 7, 4)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("날자 기반 focustime 삭제 실패 테스트")
    void deleteFocusTimeByDayFailTest() {
        // given
        Member member = members.get(0);

        //when
        service.deleteFocusTimeByYearAndMonthAndDay(member.getId(), null, null, null);

        //then
        Assertions.assertThatThrownBy(() -> service.findAllFocusTimes(member.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("날자 기반 focustime 삭제 테스트")
    void deleteFocusTimeByDaySuccessTest() {
        // given
        Member member = members.get(0);

        //when
        service.deleteFocusTimeByYearAndMonthAndDay(member.getId(), 2025, null, null);

        //then
        Assertions.assertThatThrownBy(() -> service.findAllFocusTimes(member.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("날자 기반 focustime 년-월 조건 삭제 테스트")
    void deleteFocusTimeByDaySuccess2Test() {
        // given
        Member member = members.get(0);

        //when
        service.deleteFocusTimeByYearAndMonthAndDay(member.getId(), 2025, 7, null);

        //then
        Assertions.assertThatThrownBy(() -> service.findAllFocusTimes(member.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("날자 기반 focustime 년-월-일 삭제 테스트")
    void deleteFocusTimeByDaySuccess3Test() {
        // given
        Member member = members.get(0);

        //when
        service.deleteFocusTimeByYearAndMonthAndDay(member.getId(), 2025, 7, 4);

        List<FocusTimeResponseDto> allFocusTimes = service.findAllFocusTimes(member.getId());
        //then
        Assertions.assertThat(allFocusTimes.size()).isEqualTo(2);
    }

}