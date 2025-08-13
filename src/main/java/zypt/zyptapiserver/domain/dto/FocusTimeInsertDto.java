package zypt.zyptapiserver.domain.dto;

import java.time.LocalTime;

// focusTime table insert 전용 DTO
public record FocusTimeInsertDto(LocalTime startAt, LocalTime endAt) {
}
