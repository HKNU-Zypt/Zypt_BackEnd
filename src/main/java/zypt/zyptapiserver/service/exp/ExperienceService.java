package zypt.zyptapiserver.service.exp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;
import zypt.zyptapiserver.repository.ExpRepository;
import zypt.zyptapiserver.repository.RedisCacheRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExpMultiplierManager expMultiplierManager;
    private final ExpRepository expRepository;
    private final RedisCacheRepository redisRepository;


    // 임시로 분당 1 경험치 적용
    @Transactional
    public void applyExperience(String memberId, long totalFocusTime) {

        double multiplier = expMultiplierManager.getMultiplier();
        long totalFocusTimeMinutes = totalFocusTime / 60;
        log.info("획득 경험치 ={}", totalFocusTimeMinutes);

        double result = Math.ceil(totalFocusTimeMinutes * multiplier);
        log.info("적용 경험치 ={}", result);

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

        // 현 레벨의 total_xp + result
        // 이에 맞는 레벨을 레디스에서 탐색
        LevelExpInfo levelExpInfoByExp = redisRepository.getLevelExpInfoByExp(total_exp + result + levelExp.getCurExp());
        double curExp = result + total_exp + levelExp.getCurExp() - levelExpInfoByExp.exp(); // 계산하고 남은 경험치 구함
        log.info("적용 후 레벨 ={} ,  남은 현재 경험치 : {}", levelExpInfoByExp, curExp);

        // 이를 db에 반영
        expRepository.updateLevelAndExp(levelExpInfoByExp.level(), (long) curExp);
    }

    public LevelExpInfo getLevelExpInfo(String memberId) {
        LevelExp levelExp = expRepository.findById(memberId);
        return new LevelExpInfo(levelExp.getLevel(), levelExp.getCurExp());
    }
}
