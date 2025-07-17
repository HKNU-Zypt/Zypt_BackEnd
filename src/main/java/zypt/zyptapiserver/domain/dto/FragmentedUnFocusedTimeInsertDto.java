package zypt.zyptapiserver.domain.dto;

import jakarta.validation.constraints.NotNull;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public record FragmentedUnFocusedTimeInsertDto(@NotNull LocalTime startAt,@NotNull LocalTime endAt,@NotNull UnFocusedType type) {

    // 집중하지 않은 시간 계산후 반환
    public long calculateUnfocusedDuration() {
        return ChronoUnit.SECONDS.between(startAt(), endAt());
    }
}
