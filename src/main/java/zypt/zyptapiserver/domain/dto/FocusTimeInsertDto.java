package zypt.zyptapiserver.domain.dto;

import java.time.LocalDateTime;

// focusTime table insert 전용 DTO
public record FocusTimeInsertDto(String memberId, LocalDateTime startAt, LocalDateTime endAt) {
}
