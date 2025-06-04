package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.*;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.FocusTimeJdbcRepository;
import zypt.zyptapiserver.repository.FocusTimeJpaRepository;
import zypt.zyptapiserver.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FocusTimeServiceImpl implements FocusTimeService {

    private final FocusTimeJpaRepository focusTimeJpaRepository;
    private final FocusTimeJdbcRepository focusTimeJdbcRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveFocusTime(String memberId, FocusTimeDto focusTimeDto) {
        // 멤버 엔티티 조회
        Member member = memberRepository
                .findMemberById(memberId)
                .orElseThrow(() ->
                        new MemberNotFoundException("유저를 찾을 수 없습니다. "));

        // focusTime 저장
        FocusTimeInsertDto insertDto = focusTimeDto.getFocusTimeInsertDto();

        FocusTime focusTime = focusTimeJpaRepository
                .saveFocusTime(member, focusTimeDto.createDate(), insertDto.startAt(), insertDto.endAt())
                .orElseThrow(() ->
                        new IllegalArgumentException("집중 시간을 저장할 수 없음"));

        // unfocusedTime jdbcBulkInsert로 저장
        Long sumUnFocusedTimes = focusTimeJdbcRepository.bulkInsertUnfocusedTimes(focusTime.getId(), focusTimeDto.fragmentedUnFocusedTimeInsertDtos());

        // 연관관계 설정
        member.addFocusTimes(focusTime);

        // 더티 체킹으로 집중하지않은 시간 총합을 업데이트
        focusTime.initFocusedTime(sumUnFocusedTimes);
    }

    @Override
    public List<FocusTimeResponseDto> findAllFocusTimes(String memberId) {
        List<FocusTimeResponseDto> focusTimes = focusTimeJdbcRepository.findAllFocusTimes(memberId);
        List<Long> list = focusTimes.stream().mapToLong(FocusTimeResponseDto::id).boxed().toList();
        List<FragmentedUnFocusedTimeDto> unFocusTimes = focusTimeJdbcRepository.findAllFragmentedUnFocusTimes(list);

        // TODO 이제 두개를 합쳐야함

        return List.of();
    }

    @Override
    public FocusTimeDto findFocusTime(long focus_id) {
        return null;
    }

    @Override
    public List<FocusTimeDto> findFocusTimesByLocalDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<FocusDayMarkDto> findFocusTimesByMonth(int year, int month) {
        return List.of();
    }

}
