package zypt.zyptapiserver.Service;

import org.springframework.stereotype.Component;
import zypt.zyptapiserver.domain.exp.AdminEvent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ExpMultiplierManager {
    private final AtomicReference<Double> multiplier = new AtomicReference<>(1.0);
    private volatile AdminEvent currentAdminEvent= null;

    public double getMultiplier() {
        return multiplier.get();
    }

    public boolean isAdminEventActive() {
        return currentAdminEvent != null;
    }

    public synchronized void applyAdminEvent(AdminEvent event) {
        this.currentAdminEvent = event;
    }

    public synchronized void clearAdminEvent(Double value) {
        this.currentAdminEvent = null;

        // 이벤트 해제 후 주말 여부 확인 후 주말에 적용할 배율 적용
        if (isWeekend()) {
            multiplier.set(value == null ? 2.0 : value);
        } else {
            multiplier.set(1.0);
        }
    }

    public void updateMultiplier(Double value) {

        // 현재 관리자 이벤트이면 관리자 이벤트 적용
        if (currentAdminEvent != null && currentAdminEvent.isActive()) {
            multiplier.set(currentAdminEvent.getMultiplier());

        } else if (isWeekend()) {
            multiplier.set(value == null ? 2.0 : value);

        } else {
            multiplier.set(1.0);
        }
    }

    private boolean isWeekend() {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

}
