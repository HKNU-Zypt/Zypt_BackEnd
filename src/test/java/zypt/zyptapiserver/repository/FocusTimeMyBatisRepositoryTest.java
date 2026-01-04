package zypt.zyptapiserver.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.dto.focustime.FocusTimeResponseDto;
import zypt.zyptapiserver.dto.focustime.FragmentedUnFocusedTimeDto;
import zypt.zyptapiserver.dto.focustime.FragmentedUnFocusedTimeInsertDto;
import zypt.zyptapiserver.domain.enums.UnFocusedType;
import zypt.zyptapiserver.repository.Member.MemberJdbcRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeMyBatisRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@SpringBootTest
class FocusTimeMyBatisRepositoryTest {

    @Autowired
    FocusTimeMyBatisRepository repository;

    @Autowired
    MemberJdbcRepository memberRepository;

    @Test
    @Transactional
    @DisplayName("mybatis focusTime 저장, id값 조회 확인 테스트")
    void saveOneFocusTimeTest() {
        Member member = Member.builder().id(UUID.randomUUID().toString()).nickName("abc").email("abc@gmail.com").build();
        memberRepository.save(member);

        FocusTime focusTime = repository.saveFocusTime(member, LocalDate.now(), LocalTime.now(), LocalTime.now(), 1000L).get();

        List<FragmentedUnFocusedTimeInsertDto> dto = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FragmentedUnFocusedTimeInsertDto insertDto = new FragmentedUnFocusedTimeInsertDto(LocalTime.now(), LocalTime.now().plus(Duration.ofMinutes(i)), UnFocusedType.SLEEP);
            dto.add(insertDto);
        }

        Assertions.assertThat(focusTime).isNotNull();
        Assertions.assertThat(focusTime.getId()).isNotNull();
        repository.bulkInsertUnfocusedTimes(focusTime.getId(), dto);

        FocusTimeResponseDto focusTimeResponseDto = repository.findFocusTime(focusTime.getId()).orElseThrow(() -> new NoSuchElementException("존재하지 않음"));

        List<FragmentedUnFocusedTimeDto> unFocusTimes = repository.findAllFragmentedUnFocusTimes(List.of(focusTime.getId()));

        Assertions.assertThat(focusTimeResponseDto.getId()).isEqualTo(focusTime.getId());
        Assertions.assertThat(unFocusTimes.size()).isEqualTo(10);

        log.info("focus_id = {}", focusTime.getId());
    }



}