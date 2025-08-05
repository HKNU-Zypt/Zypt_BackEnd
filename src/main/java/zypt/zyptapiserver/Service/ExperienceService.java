package zypt.zyptapiserver.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zypt.zyptapiserver.repository.ExpRepository;
import zypt.zyptapiserver.repository.RedisRepository;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExpMultiplierManager expMultiplierManager;
    private final ExpRepository expRepository;

    private final RedisRepository redisRepository;

    public void applyExperience(String memberId, long totalFocusTime) {

        double multiplier = expMultiplierManager.getMultiplier();
        long result = (long) Math.ceil(totalFocusTime * multiplier);

        // 레벨, 경험치 정보 조회
//        expRepository.findById(memberId);

        // 현재 레벨의 total_xp 조회
//        redisRepository.findLevelInfo();

        // 현 레벨의 total_xp + result
        // 이에 맞는 레벨을 레디스에서 탐색

        // 이를 db에 반영
    }
}
