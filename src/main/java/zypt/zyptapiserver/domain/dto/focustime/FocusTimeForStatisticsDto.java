package zypt.zyptapiserver.domain.dto.focustime;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class FocusTimeForStatisticsDto {
    private final long id;
    private final LocalTime startAt;
    private final LocalTime endAt;
    private final LocalDate createDate;

    @QueryProjection
    public FocusTimeForStatisticsDto(long id, LocalTime startAt, LocalTime endAt, LocalDate createDate) {
        this.id = id;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createDate = createDate;
    }
}
