package zypt.zyptapiserver.dto;

import java.time.LocalDateTime;

public record ErrorReportDto(String reqId, String memberId, String body, LocalDateTime date) {
}
