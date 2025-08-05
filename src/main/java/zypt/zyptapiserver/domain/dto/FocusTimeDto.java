package zypt.zyptapiserver.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 클라이언트에게 받은 DTO
public record FocusTimeDto(
        @NotNull
        @JsonFormat(pattern = "HH-mm-ss")
        LocalTime startAt,

        @NotNull
        @JsonFormat(pattern = "HH-mm-ss")
        LocalTime endAt,

        @NotNull LocalDate createDate,
        @NotNull List<FragmentedUnFocusedTimeInsertDto> fragmentedUnFocusedTimeInsertDtos) {

    public FocusTimeInsertDto getFocusTimeInsertDto() {
        return new FocusTimeInsertDto(startAt(), endAt());
    }

}
