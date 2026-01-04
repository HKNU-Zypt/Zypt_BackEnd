package zypt.zyptapiserver.dto.focustime;

import jakarta.validation.constraints.NotNull;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public record FragmentedUnFocusedTimeInsertDto(
        @NotNull
        LocalTime startAt,

        @NotNull
        LocalTime endAt,

        @NotNull UnFocusedType type) {

    // 집중하지 않은 시간 계산후 반환
    public long calculateUnfocusedDuration() {
        if (endAt.isBefore(startAt)) {
            int endSecond = endAt.getSecond() + (24 * 3600); // 24시간을 더한 값
            int startSecond = startAt.getSecond();
            int result = endSecond - startSecond;
            return result;
        }

        return ChronoUnit.SECONDS.between(startAt, endAt);
    }
}
