package zypt.zyptapiserver.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zypt.zyptapiserver.domain.FragmentedUnfocusedTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 클라이언트에게 전송할 DTO
@Getter
@AllArgsConstructor
public class FocusTimeResponseDto {
    private final Long id;
    private final String memberId;
    private final LocalTime startAt;
    private final LocalTime endAt;
    private final LocalDate createDate;
    private List<FragmentedUnFocusedTimeDto> unFocusedTimeDtos;

    public void addUnFocusedTimeDtos(List<FragmentedUnFocusedTimeDto> dtos) {
        unFocusedTimeDtos = dtos;
    }
}
