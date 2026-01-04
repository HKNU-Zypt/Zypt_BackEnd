package zypt.zyptapiserver.service.focustime;

import zypt.zyptapiserver.dto.focustime.FocusTimeDto;
import zypt.zyptapiserver.dto.focustime.FocusTimeResponseDto;

import java.util.List;

public interface FocusTimeService {

    /**
     * 집중 시간 데이터를 저장
     * @param memberId 유저 id
     * @param focusTimeDto
     */
    long saveFocusTime(String memberId, FocusTimeDto focusTimeDto);


    /**
     *  멤버가 가진 모든 집중 데이터 조회
     * @param memberId
     * @return FocusTimeDto 리스트
     */
    List<FocusTimeResponseDto> findAllFocusTimes(String memberId);

    /**
     * 집중 id로 단건 집중 데이터 조회
     *
     * @param focus_id
     * @return FocusTimeDTO
     */
    FocusTimeResponseDto findFocusTime(long focus_id);

    /**
     * 해당 날짜의 집중 데이터를 조회
     * @param year 날짜 데이터 YYYY-MM-DD
     * @return FocusTimeDto 리스트
     */
    List<FocusTimeResponseDto> findFocusTimesByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day);



    void deleteFocusTimeByYearAndMonthAndDay(String memberId, Integer year, Integer month, Integer day);

    void deleteFocusTimeById(String memberId, Long focusId);

}
