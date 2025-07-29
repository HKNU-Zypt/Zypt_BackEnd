package zypt.zyptapiserver.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpWeekendScheduler {
    private final ExpMultiplierManager manager;

    public ExpWeekendScheduler(ExpMultiplierManager manager) {
        this.manager = manager;
    }

    // 토요일 00시에 2배 이벤트 시작
    @Scheduled(cron = "0 0 0 ? * SAT")
    public void startWeekendEvent() {
        if (!manager.isAdminEventActive()) {
            manager.updateMultiplier(2.0);
        }
    }

    // 월요일 00시에 원복
    @Scheduled(cron = "0 0 0 ? * MON")
    public void endWeekendEvent() {
        if (!manager.isAdminEventActive()) {
            manager.updateMultiplier(1.0);
        }
    }

}
