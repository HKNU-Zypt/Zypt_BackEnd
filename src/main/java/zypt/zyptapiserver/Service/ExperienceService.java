package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zypt.zyptapiserver.domain.LevelExp;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;
import zypt.zyptapiserver.repository.ExpRepository;
import zypt.zyptapiserver.repository.RedisCacheRepository;
import zypt.zyptapiserver.repository.RedisRepository;

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

        double result = Math.ceil(totalFocusTimeMinutes * multiplier);

        // 레벨, 경험치 정보 조회
        LevelExp levelExp = expRepository.findById(memberId);

        // 현재 레벨의 total_xp 조회
        Double total_exp = redisRepository.getExpByLevel(levelExp.getLevel());

        // 현 레벨의 total_xp + result
        // 이에 맞는 레벨을 레디스에서 탐색
        LevelExpInfo levelExpInfoByExp = redisRepository.getLevelExpInfoByExp(total_exp + result);
        double curExp = result + total_exp -  levelExpInfoByExp.exp(); // 계산하고 남은 경험치 구함

        // 이를 db에 반영
        expRepository.updateLevelAndExp(levelExpInfoByExp.level(), (long) curExp);
    }

    @Transactional(readOnly = true)
    public LevelExpInfo getLevelExpInfo(String memberId) {
        LevelExp levelExp = expRepository.findById(memberId);
        return new LevelExpInfo(levelExp.getLevel(), levelExp.getCurExp());
    }
}
