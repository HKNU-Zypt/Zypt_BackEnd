package zypt.zyptapiserver.domain.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

// focusTime table insert 전용 DTO
public record FocusTimeInsertDto(String memberId, LocalTime startAt, LocalTime endAt) {
}
