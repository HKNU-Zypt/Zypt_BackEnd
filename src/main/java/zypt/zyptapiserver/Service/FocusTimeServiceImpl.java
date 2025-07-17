package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.auth.exception.FocusTimeNotFoundException;
import zypt.zyptapiserver.auth.exception.FocusTimeSaveFailedException;
import zypt.zyptapiserver.auth.exception.InvalidParamException;
import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.*;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.repository.FocusTimeRepository;
import zypt.zyptapiserver.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class FocusTimeServiceImpl implements FocusTimeService {

    private final FocusTimeRepository focusTimeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveFocusTime(String memberId, FocusTimeDto focusTimeDto) {
        // 멤버 엔티티 조회
        Member member = memberRepository
                .findMemberById(memberId)
                .orElseThrow(() ->
                        new MemberNotFoundException("유저를 찾을 수 없습니다. "));

        // 총 집중하지 않은 시간 합
        long sumUnFocusedTimes = focusTimeDto
                .fragmentedUnFocusedTimeInsertDtos()
                .stream()
                .mapToLong(FragmentedUnFocusedTimeInsertDto::calculateUnfocusedDuration)
                .sum();

        // focusTime 저장
        FocusTimeInsertDto insertDto = focusTimeDto.getFocusTimeInsertDto();

        FocusTime focusTime = focusTimeRepository
                .saveFocusTime(member, focusTimeDto.createDate(), insertDto.startAt(), insertDto.endAt(), sumUnFocusedTimes)
                .orElseThrow(() ->
                        new FocusTimeSaveFailedException("집중 시간을 저장할 수 없음"));

        // unfocusedTime jdbcBulkInsert로 저장
        focusTimeRepository.bulkInsertUnfocusedTimes(focusTime.getId(), focusTimeDto.fragmentedUnFocusedTimeInsertDtos());
    }

    @Override
    public List<FocusTimeResponseDto> findAllFocusTimes(String memberId) {
        List<FocusTimeResponseDto> focusTimes = focusTimeRepository.findAllFocusTimes(memberId);
        List<Long> list = focusTimes.stream().mapToLong(FocusTimeResponseDto::getId).distinct().boxed().toList(); // focus_id 리스트 추출

        if (list.isEmpty()) {
            throw new FocusTimeNotFoundException("focus id가 하나도 없습니다.");
        }

        List<FragmentedUnFocusedTimeDto> unFocusTimes = focusTimeRepository.findAllFragmentedUnFocusTimes(list); // in 검색

        // id값을 FragmentedUnFocusedTimeDto에 하나하나 매핑해야함
        Map<Long, List<FragmentedUnFocusedTimeDto>> unFocusedTimesMap  =
                unFocusTimes.stream()
                        .collect(Collectors.groupingBy(FragmentedUnFocusedTimeDto::focusId));

        // 매핑
        return focusTimes.stream().map(focusTime -> {
                    List<FragmentedUnFocusedTimeDto> associatedUnFocusedTimes =
                            unFocusedTimesMap.getOrDefault(focusTime.getId(), new ArrayList<>());

                    focusTime.addUnFocusedTimeDtos(associatedUnFocusedTimes);
                    return focusTime;
                })
                .collect(Collectors.toList());
    }

    @Override
    public FocusTimeResponseDto findFocusTime(long focus_id) {
        List<FragmentedUnFocusedTimeDto> unFocusTimes = focusTimeRepository.findAllFragmentedUnFocusTimes(List.of(focus_id));

        FocusTimeResponseDto focusTimeResponseDto = focusTimeRepository.findFocusTime(focus_id)
                .orElseThrow(() ->
                        new FocusTimeNotFoundException("focus_id에 해당하는 FocusTime이 존재하지 않습니다."));

        focusTimeResponseDto.addUnFocusedTimeDtos(unFocusTimes);

        return focusTimeResponseDto;
    }


    @Override
    public List<FocusTimeResponseDto> findFocusTimesByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day) {

        if (validateYearMonthDay(year, month, day)) {
            throw new InvalidParamException("년-월-일 순서에 맞게 날짜를 입력");
        }

        List<FocusTimeResponseDto> focusTimeDtos = focusTimeRepository.findFocusTimesByYearAndMonthAndDay(memberId, year, month, day);

        List<Long> focusIds = focusTimeDtos.stream()
                .mapToLong(FocusTimeResponseDto::getId)
                .distinct()
                .boxed()
                .toList();

        if (focusIds.isEmpty()) {
            throw new FocusTimeNotFoundException("focus id가 하나도 없습니다.");
        }

        List<FragmentedUnFocusedTimeDto> unFocusTimes = focusTimeRepository.findAllFragmentedUnFocusTimes(focusIds);

        Map<Long, List<FragmentedUnFocusedTimeDto>> unFocusMap = unFocusTimes.stream().collect(Collectors.groupingBy(FragmentedUnFocusedTimeDto::focusId));

        focusTimeDtos.stream().map(focusTime -> {
            Long id = focusTime.getId();
            List<FragmentedUnFocusedTimeDto> fragmentedUnFocusedTimeDtos = unFocusMap.getOrDefault(id, new ArrayList<>());
            focusTime.addUnFocusedTimeDtos(fragmentedUnFocusedTimeDtos);

            return focusTime;
        });


        return focusTimeDtos;
    }

    @Override
    public void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day) {
        if (validateYearMonthDay(year, month, day)) {
            throw new InvalidParamException("년-월-일 순서에 맞게 날짜를 입력");
        }
        List<Long> ids = focusTimeRepository.findFocusTimeIdsByDate(memberId, year, month, day);

        if (ids.isEmpty()) {
            throw new FocusTimeNotFoundException("삭제할 focusTime 존재하지 않음");
        }

        focusTimeRepository.deleteFocusTimeByYearAndMonthAndDay(memberId, year, month, day, ids);

    }

    private static boolean validateYearMonthDay(Integer year, Integer month, Integer day) {
        return year == null && month != null
                || year == null && day != null
                || day != null && month == null;
    }
}
