package zypt.zyptapiserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zypt.zyptapiserver.service.exp.ExpMultiplierManager;
import zypt.zyptapiserver.domain.exp.AdminEvent;
import zypt.zyptapiserver.repository.RedisCacheRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ExpAdminController {


    private final ExpMultiplierManager manager;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final RedisCacheRepository redisCacheRepository;

    @PostMapping("/exp_event")
    public ResponseEntity<String> expEvent(@RequestParam double multiplier,
                                           @RequestParam LocalDateTime startTime,
                                           @RequestParam LocalDateTime endTime) {


        AdminEvent event = new AdminEvent(multiplier, startTime, endTime);
        manager.applyAdminEvent(event);

        long delayUntilStart = Duration.between(LocalDateTime.now(), startTime).toSeconds();
        long duration = Duration.between(startTime, endTime).toSeconds();

        // 시작 시간이 이미 지났다면 바로 시작
        if (delayUntilStart < 0) delayUntilStart = 0;

        scheduler.schedule(() -> manager.updateMultiplier(null), delayUntilStart, TimeUnit.SECONDS);
        scheduler.schedule(() -> manager.clearAdminEvent(null), delayUntilStart + duration, TimeUnit.SECONDS);

        return ResponseEntity.ok("Admin event 스케줄링 됨 " + multiplier + "배 적용 " + startTime + " 부터  " + endTime + " 까지 적용");
    }

    @PostMapping("/exp_table")
    public ResponseEntity<String> init() {
        redisCacheRepository.initializeLevelExpTable();
        return ResponseEntity.ok("초기화 완료");
    }

}
