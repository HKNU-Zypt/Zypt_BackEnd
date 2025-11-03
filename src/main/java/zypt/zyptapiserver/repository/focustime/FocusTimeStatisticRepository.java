package zypt.zyptapiserver.repository.focustime;

import zypt.zyptapiserver.domain.dto.focustime.FocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.dto.focustime.UnFocusTimeForStatisticsDto;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalDate;
import java.util.List;

public interface FocusTimeStatisticRepository {

    public List<FocusTimeForStatisticsDto> findFocusTimeForStatistics(String memberId, LocalDate startDate, LocalDate endDate);

    public List<UnFocusTimeForStatisticsDto> findUnFocusTimeForStatistics(List<Long> ids, UnFocusedType unFocusedType);
}
