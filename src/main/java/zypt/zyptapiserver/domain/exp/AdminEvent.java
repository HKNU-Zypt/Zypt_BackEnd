package zypt.zyptapiserver.domain.exp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AdminEvent {
    private final double multiplier;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;


    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

}
