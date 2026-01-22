package zypt.zyptapiserver.service.exp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;
import zypt.zyptapiserver.repository.ExpRepository;
import zypt.zyptapiserver.repository.RedisCacheRepository;

import javax.swing.border.Border;
import java.io.OptionalDataException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExpProcess expProcess;

    //  분당 1 경험치 적용
    // 기본 3번 재시도
    @Retryable(
            retryFor = {OptionalDataException.class, ObjectOptimisticLockingFailureException.class},
            backoff = @Backoff(delay = 100)
    )
    public void applyExperience(String memberId, long totalFocusTime) {
        expProcess.applyExperience(memberId, totalFocusTime);
    }
}
