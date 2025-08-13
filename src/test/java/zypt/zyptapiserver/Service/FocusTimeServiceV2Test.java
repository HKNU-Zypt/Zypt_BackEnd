package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.FocusTimeDto;
import zypt.zyptapiserver.domain.dto.FocusTimeForStatisticsResponseDto;
import zypt.zyptapiserver.domain.dto.FragmentedUnFocusedTimeInsertDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@RequiredArgsConstructor
public class FocusTimeServiceV2Test {

    @Autowired
    FocusTimeServiceImplV2 focusTimeServiceImplV2;

    @Autowired
    MemberService memberService;



    @BeforeEach
    void init() {
        Member member = memberService.saveMember(Member.builder().nickName("abc").email("abc@gmail.com").socialId("123").socialType(SocialType.KAKAO).build());

        String id = member.getId();

        List<FragmentedUnFocusedTimeInsertDto> dto = new ArrayList<>();
        dto.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(11,59,59), LocalTime.of(12,5), UnFocusedType.DISTRACTED));
        dto.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(12,30), LocalTime.of(13, 22,11), UnFocusedType.DISTRACTED));
        dto.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(13,59,59), LocalTime.of(14,1), UnFocusedType.DISTRACTED));


        focusTimeServiceImplV2.saveFocusTime(id, new FocusTimeDto(LocalTime.of(11,32,55), LocalTime.of(14,1,0), LocalDate.now(), dto));

        dto.clear();
        dto.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(23, 59, 59), LocalTime.of(1, 1), UnFocusedType.DISTRACTED));
        dto.add(new FragmentedUnFocusedTimeInsertDto(LocalTime.of(1, 30, 59), LocalTime.of(1, 45), UnFocusedType.DISTRACTED));
        focusTimeServiceImplV2.saveFocusTime(id, new FocusTimeDto(LocalTime.of(23,59,59), LocalTime.of(3,1), LocalDate.now().minusDays(1), dto));

    }

    @Test
    @Transactional
    void run() {
        Member member = memberService.findMemberBySocialId(SocialType.KAKAO, "123");

        for (int i = 0; i < 2; i++) {
            FocusTimeForStatisticsResponseDto times = focusTimeServiceImplV2.findFocusTimesForStatisticsByDateRange(member.getId(), LocalDate.now().minusDays(i), LocalDate.now());
            log.info("result = {}", times);

        }
    }

}
