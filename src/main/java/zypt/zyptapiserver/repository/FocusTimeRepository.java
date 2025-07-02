package zypt.zyptapiserver.repository;

import zypt.zyptapiserver.domain.FocusTime;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.domain.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface FocusTimeRepository {

    // FocusTime 저장
    Optional<FocusTime> saveFocusTime(Member member, LocalDate date, LocalTime start_at, LocalTime end_at);

    // UnfocusedTimes 벌크 삽입
    Long bulkInsertUnfocusedTimes(Long focusId, List<FragmentedUnFocusedTimeInsertDto> unfocusedTimes);

    /**
     * 특정 회원의 모든 집중 시간 데이터를 조회
     * @param memberId
     * @return
     */
    List<FocusTimeResponseDto> findAllFocusTimes(String memberId);

    List<FragmentedUnFocusedTimeDto> findAllFragmentedUnFocusTimes(List<Long> focusIdList);

    /**
     * 집중 id로 단건 집중 데이터 조회
     *
     * @param focusId
     * @return FocusTimeDTO
     */
    Optional<FocusTimeResponseDto> findFocusTime(long focusId);


    /**
     * 년-월-일 조회
     * @param year
     * @param month
     * @param day
     * @return
     */
    List<FocusTimeResponseDto> findFocusTimesByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day);



    /**
     * 한 달의 집중 데이터 존재 여부 조회
     * 이는 달력에서 집중 데이터가 있으면 그 날짜에 표시하는 용도로 사용
     * @param year
     * @param month
     * @return FocusDayMarkDto 리스트
     */
    List<Integer> findFocusTimesByMonth(String memberId,int year, int month);


    void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day);

}
