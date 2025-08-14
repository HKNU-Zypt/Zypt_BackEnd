package zypt.zyptapiserver.domain.dto.focustime;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@ToString
public class FocusTimeForStatisticsResponseDto {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int[] focusScoresPerHours;


    public FocusTimeForStatisticsResponseDto(LocalDate startDate, LocalDate endDate,  int[] focusScoresPerHours) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.focusScoresPerHours = focusScoresPerHours;
    }
}
