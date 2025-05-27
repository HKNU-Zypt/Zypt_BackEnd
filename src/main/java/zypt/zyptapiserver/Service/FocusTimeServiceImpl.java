package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.FocusTimeDto;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.FocusTimeRepository;
import zypt.zyptapiserver.repository.MemberRepository;


@Service
@RequiredArgsConstructor
public class FocusTimeServiceImpl implements FocusTimeService{

    private final FocusTimeRepository focusTimeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveFocusTime(String id, FocusTimeDto focusTimeDto) {
        // 멤버 엔티티 조회
        Member member = memberRepository
                .findMemberById(id)
                .orElseThrow(() ->
                        new MemberNotFoundException("유저를 찾을 수 없습니다. "));

        // focusTime 저장
        FocusTime focusTime = focusTimeRepository
                .saveFocusTime(member, focusTimeDto.getFocusTimeInsertDto())
                .orElseThrow(() ->
                        new IllegalArgumentException("집중 시간을 저장할 수 없음"));

        // unfocusedTime jdbcBulkInsert로 저장
        Long sumUnFocusedTimes = focusTimeRepository.bulkInsertUnfocusedTimes(focusTime.getId(), focusTimeDto.fragmentedUnFocusedTimeInsertDtos());

        // 연관관계 설정
        member.addFocusTimes(focusTime);

        // 더티 체킹으로 집중하지않은 시간 총합을 업데이트
        focusTime.initFocusedTime(sumUnFocusedTimes);
    }

}
