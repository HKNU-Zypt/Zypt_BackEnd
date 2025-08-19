package zypt.zyptapiserver.service.focustime;

import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsResponseDto;

import java.time.LocalDate;

public interface FocusTimeStatisticsService {

    FocusTimeForStatisticsResponseDto findFocusTimesForStatisticsByDateRange(String memberId, LocalDate startDate, LocalDate endDate);
}
