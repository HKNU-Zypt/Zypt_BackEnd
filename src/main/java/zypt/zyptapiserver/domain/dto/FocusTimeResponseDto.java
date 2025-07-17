package zypt.zyptapiserver.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import zypt.zyptapiserver.domain.FragmentedUnfocusedTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 클라이언트에게 전송할 DTO
@Getter
public class FocusTimeResponseDto {
    private final Long id;
    private final String memberId;
    private final LocalTime startAt;
    private final LocalTime endAt;
    private final LocalDate createDate;
    private List<FragmentedUnFocusedTimeDto> unFocusedTimeDtos;

    @QueryProjection
    public FocusTimeResponseDto(Long id, String memberId, LocalTime startAt, LocalTime endAt, LocalDate createDate) {
        this.id = id;
        this.memberId = memberId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createDate = createDate;
    }

    public void addUnFocusedTimeDtos(List<FragmentedUnFocusedTimeDto> dtos) {
        unFocusedTimeDtos = dtos;
    }
}
