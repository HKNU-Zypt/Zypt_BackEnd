package zypt.zyptapiserver.service.focustime;

import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsResponseDto;

import java.time.LocalDate;

public interface FocusTimeStatisticsService {
    // 집중한 시간 통계
    FocusTimeForStatisticsResponseDto findFocusTimesForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate);
    // 졸음 횟수 통계
    FocusTimeForStatisticsResponseDto findSleepCountForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate);
    // 집중 안함 횟수 통계
    FocusTimeForStatisticsResponseDto findDistractedCountForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate);

}
