package zypt.zyptapiserver.domain.dto.focustime;

import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public record FragmentedUnFocusedTimeDto(Long id, Long focusId, LocalTime startAt, LocalTime endAt, UnFocusedType type, Long unfocusedTime) {

    // 집중하지 않은 시간 계산후 반환
    public long calculateUnfocusedDuration() {
        return ChronoUnit.SECONDS.between(startAt(), endAt());
    }
}
