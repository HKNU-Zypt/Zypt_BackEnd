package zypt.zyptapiserver.domain.dto;

import java.time.LocalDateTime;

public record ErrorReportDto(String reqId, String memberId, String body, LocalDateTime date) {
}
