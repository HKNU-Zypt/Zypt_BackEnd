package zypt.zyptapiserver.domain.dto.focustime;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 클라이언트에게 받은 DTO
public record FocusTimeDto(
        @NotNull
        LocalTime startAt,

        @NotNull
        LocalTime endAt,

        @NotNull LocalDate createDate,
        @NotNull List<FragmentedUnFocusedTimeInsertDto> fragmentedUnFocusedTimeInsertDtos) {

    public FocusTimeInsertDto getFocusTimeInsertDto() {
        return new FocusTimeInsertDto(startAt(), endAt());
    }

}
