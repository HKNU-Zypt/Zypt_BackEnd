package zypt.zyptapiserver.domain.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 클라이언트에게 받은 DTO
public record FocusTimeDto(String memberId, LocalTime startAt, LocalTime endAt, LocalDate createDate,
                           List<FragmentedUnFocusedTimeInsertDto> fragmentedUnFocusedTimeInsertDtos) {

    public FocusTimeInsertDto getFocusTimeInsertDto() {
        return new FocusTimeInsertDto(memberId(), startAt(), endAt());
    }

}
