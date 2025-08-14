package zypt.zyptapiserver.domain.dto.focustime;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class UnFocusTimeForStatisticsDto {
    private final LocalTime startAt;
    private final LocalTime endAt;
    private final Long unfocusedTime;

    @QueryProjection
    public UnFocusTimeForStatisticsDto(LocalTime startAt, LocalTime endAt, Long unfocusedTime) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.unfocusedTime = unfocusedTime;
    }
}
