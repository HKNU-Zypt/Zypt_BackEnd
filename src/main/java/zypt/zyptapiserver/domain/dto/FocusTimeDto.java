package zypt.zyptapiserver.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

// 클라이언트에게 받은 DTO
public record FocusTimeDto(String memberId, LocalDateTime startAt, LocalDateTime endAt,
                           List<FragmentedUnFocusedTimeInsertDto> fragmentedUnFocusedTimeInsertDtos) {

    public FocusTimeInsertDto getFocusTimeInsertDto() {
        return new FocusTimeInsertDto(memberId(), startAt(), endAt());
    }

}
