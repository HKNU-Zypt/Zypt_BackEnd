package zypt.zyptapiserver.service.focustime;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.focustime.FocusTimeDto;
import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsResponseDto;
import zypt.zyptapiserver.domain.dto.focustime.FragmentedUnFocusedTimeInsertDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.domain.enums.UnFocusedType;
import zypt.zyptapiserver.service.member.MemberService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@SpringBootTest
class FocusTimeStatisticsServiceImplTest {
    @Autowired
    FocusTimeStatisticsService focusTimeStatisticsServiceImpl;
    @Autowired
    FocusTimeService focusTimeService;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void init() {
        Member member = memberService.saveMember(
                Member.builder()
                        .nickName("abc")
                        .email("abc@gmail.com")
                        .socialId("123")
                        .socialType(SocialType.KAKAO)
                        .build()
        );

        String id = member.getId();

        // ------------------------
        // [1] 어제 (23:30 → 다음날 11:05) - DISTRACTED + SLEEP 혼합
        // ------------------------
        List<FragmentedUnFocusedTimeInsertDto> dto1 = new ArrayList<>();
        dto1.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(23, 40), LocalTime.of(15, 50), UnFocusedType.DISTRACTED));
        dto1.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(23, 51), LocalTime.of(11, 59, 59), UnFocusedType.SLEEP));

        focusTimeService.saveFocusTime(
                id,
                new FocusTimeDto(
                        LocalTime.of(23, 30),
                        LocalTime.of(15, 55), // 다음날 아침까지 이어짐
                        LocalDate.now().minusDays(1),
                        dto1
                )
        );

        // ------------------------
        // [2] 오늘 (08:00 → 16:30) - 다양한 구간
        // ------------------------
        List<FragmentedUnFocusedTimeInsertDto> dto2 = new ArrayList<>();
        dto2.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(8, 15), LocalTime.of(8, 30), UnFocusedType.DISTRACTED));
        dto2.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(10, 45), LocalTime.of(11, 0), UnFocusedType.SLEEP));
        dto2.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(12, 0), LocalTime.of(12, 30), UnFocusedType.DISTRACTED));
        dto2.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(14, 20), LocalTime.of(14, 50), UnFocusedType.SLEEP));
        dto2.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(16, 0), LocalTime.of(16, 20), UnFocusedType.DISTRACTED));

        focusTimeService.saveFocusTime(
                id,
                new FocusTimeDto(
                        LocalTime.of(8, 0),
                        LocalTime.of(16, 30),
                        LocalDate.now(),
                        dto2
                )
        );

        // ------------------------
        // [3] 오늘 (22:10 → 23:59:59) - SLEEP 비율 높은 패턴
        // ------------------------
        List<FragmentedUnFocusedTimeInsertDto> dto3 = new ArrayList<>();
        dto3.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(22, 15), LocalTime.of(22, 40), UnFocusedType.SLEEP));
        dto3.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(23, 0), LocalTime.of(23, 30), UnFocusedType.SLEEP));
        dto3.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(23, 45), LocalTime.of(23, 59, 59), UnFocusedType.DISTRACTED));

        focusTimeService.saveFocusTime(
                id,
                new FocusTimeDto(
                        LocalTime.of(22, 10),
                        LocalTime.of(23, 59, 59),
                        LocalDate.now(),
                        dto3
                )
        );

        // ------------------------
        // [4] 내일 (09:00 → 18:00) - 긴 세션, 중간중간 짧은 DISTRACTED
        // ------------------------
        List<FragmentedUnFocusedTimeInsertDto> dto4 = new ArrayList<>();
        dto4.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(9, 50), LocalTime.of(10, 5), UnFocusedType.DISTRACTED));
        dto4.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(11, 40), LocalTime.of(12, 0), UnFocusedType.SLEEP));
        dto4.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(14, 0), LocalTime.of(14, 10), UnFocusedType.DISTRACTED));
        dto4.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(15, 45), LocalTime.of(16, 0), UnFocusedType.SLEEP));
        dto4.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(17, 40), LocalTime.of(17, 50), UnFocusedType.DISTRACTED));

        focusTimeService.saveFocusTime(
                id,
                new FocusTimeDto(
                        LocalTime.of(9, 0),
                        LocalTime.of(18, 0),
                        LocalDate.now().plusDays(1),
                        dto4
                )
        );
    }


    @Test
    @DisplayName("기존 통계 조회")
    @Transactional
    void findStatistics() {
        Member member = memberService.findMemberBySocialId(SocialType.KAKAO, "123");

        FocusTimeForStatisticsResponseDto focusTimeForStatisticsResponseDto = focusTimeStatisticsServiceImpl.findFocusTimesForStatisticsByDateRange(member.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        int[] scoresPerHours = focusTimeForStatisticsResponseDto.getFocusScoresPerHours();

        log.info("{} ~ {}", focusTimeForStatisticsResponseDto.getStartDate(), focusTimeForStatisticsResponseDto.getEndDate());
        log.info("통계 24H");
        for (int i = 0; i < 24; i++) {
            log.info("{}시  || {}", i, scoresPerHours[i]);
        }
    }

    @Test
    @DisplayName("비집중 통계 (졸음 카운팅)")
    @Transactional
    void findStatisticsWithSleep() {
        Member member = memberService.findMemberBySocialId(SocialType.KAKAO, "123");

        FocusTimeForStatisticsResponseDto sleepDto = focusTimeStatisticsServiceImpl.findSleepCountForStatisticsByDateRange(member.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        int[] scoresPerHours = sleepDto.getFocusScoresPerHours();
        log.info("{} ~ {}", sleepDto.getStartDate(), sleepDto.getEndDate());
        log.info("졸음 카운트 24H");
        for (int i = 0; i < 24; i++) {
            log.info("{}시  || {}", i, "=".repeat(scoresPerHours[i]));
        }

    }
    @Test
    @DisplayName("비집중 통계 (비집중 카운팅)")
    @Transactional
    void findStatisticsWithDistracted() {
        Member member = memberService.findMemberBySocialId(SocialType.KAKAO, "123");

        FocusTimeForStatisticsResponseDto sleepDto = focusTimeStatisticsServiceImpl.findDistractedCountForStatisticsByDateRange(member.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        int[] scoresPerHours = sleepDto.getFocusScoresPerHours();
        log.info("{} ~ {}", sleepDto.getStartDate(), sleepDto.getEndDate());
        log.info("비집중 카운트 24H");
        for (int i = 0; i < 24; i++) {
            log.info("{}시  || {}", i, "=".repeat(scoresPerHours[i]));
        }

    }
}