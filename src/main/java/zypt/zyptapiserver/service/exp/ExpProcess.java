package zypt.zyptapiserver.service.exp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;
import zypt.zyptapiserver.repository.ExpRepository;
import zypt.zyptapiserver.repository.RedisCacheRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpProcess {
    private final ExpMultiplierManager expMultiplierManager;
    private final RedisCacheRepository redisRepository;
    private final ExpRepository expRepository;

    @Transactional
    public void applyExperience(String memberId, long totalFocusTime) {

        double multiplier = expMultiplierManager.getMultiplier();
        long totalFocusTimeMinutes = totalFocusTime / 60;

        double earnedExp = Math.ceil(totalFocusTimeMinutes * multiplier);
        log.info("획득 경험치 ={}", earnedExp);

        // 레벨, 경험치 정보 조회
        LevelExp levelExp = expRepository.findById(memberId);
        log.info("현재 레벨 정보 ={}  경험치 = {}", levelExp.getLevel(), levelExp.getCurExp());
        // 현재 레벨의 total_xp 조회
        Double total_exp = redisRepository.getExpByLevel(levelExp.getLevel());
        log.info("현재레벨의 total_xp 조회 ={}", total_exp);



        if (total_exp == null) {
            log.error("경험치 테이블 존재 X, 빠른 조치 ");
            throw new IllegalStateException("경험치 테이블 존재하지 않음");
        }

        // 현 레벨의 total_xp + earnedExp
        // 이에 맞는 레벨을 레디스에서 탐색
        double exp = total_exp + earnedExp + levelExp.getCurExp();
        LevelExpInfo levelExpInfoByExp = redisRepository.getLevelExpInfoByExp(exp);

        double curExp = exp - levelExpInfoByExp.exp(); // 계산하고 남은 경험치 구함
        log.info("적용 후 레벨 ={} ,  남은 현재 경험치 : {}", levelExpInfoByExp, curExp);

        // 이를 db에 반영
        levelExp.applyLevelInfo(levelExpInfoByExp.level(), (long) curExp);
    }
}
