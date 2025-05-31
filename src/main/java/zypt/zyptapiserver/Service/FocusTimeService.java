package zypt.zyptapiserver.Service;

import zypt.zyptapiserver.domain.dto.FocusDayMarkDto;
import zypt.zyptapiserver.domain.dto.FocusTimeDto;

import java.time.LocalDate;
import java.util.List;

public interface FocusTimeService {

    /**
     * 집중 시간 데이터를 저장
     * @param memberId 유저 id
     * @param focusTimeDto
     */
    void saveFocusTime(String memberId, FocusTimeDto focusTimeDto);


    /**
     *  멤버가 가진 모든 집중 데이터 조회
     * @param memberId
     * @return FocusTimeDto 리스트
     */
    List<FocusTimeDto> findAllFocusTimes(String memberId);

    /**
     * 집중 id로 단건 집중 데이터 조회
     * @param focus_id
     * @return FocusTimeDTO
     */
    FocusTimeDto findFocusTime(long focus_id);

    /**
     * 해당 날짜의 집중 데이터를 조회
     * @param date 날짜 데이터 YYYY-MM-DD
     * @return FocusTimeDto 리스트
     */
    List<FocusTimeDto> findFocusTimesByLocalDate(LocalDate date);


    /**
     * 한 달의 집중 데이터 존재 여부 조회
     * 이는 달력에서 집중 데이터가 있으면 그 날짜에 표시하는 용도로 사용
     * @param year
     * @param month
     * @return FocusDayMarkDto 리스트
     */
    List<FocusDayMarkDto> findFocusTimesByMonth(int year, int month);
}
