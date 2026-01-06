package zypt.zyptapiserver.repository;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;

import java.util.Set;

public class RedisCacheRepository {

    private static final String EXP_KEY = "EXP_TABLE";
    private final ZSetOperations<String, String> zSetOperations;
    private final StringRedisTemplate template;

    public RedisCacheRepository(StringRedisTemplate redisTemplate) {
        this.zSetOperations = redisTemplate.opsForZSet();
        this.template = redisTemplate;
    }

    // 레벨 테이블 생성
    public void addLevelExp(int level, double exp) {
        zSetOperations.add(EXP_KEY, String.valueOf(level), exp);
    }

    /**
     * 현재 총 경험치로 레벨 측정
     * @param curExp
     * @return
     */
    public LevelExpInfo getLevelExpInfoByExp(double curExp) {

        // 현재 경험치에 맞는 적절한 레벨을 찾음
        Set<ZSetOperations.TypedTuple<String>> result
                = zSetOperations.reverseRangeByScoreWithScores(EXP_KEY, 0, curExp, 0, 1);

        if (result == null || result.isEmpty()) {
            return null;
        }

        ZSetOperations.TypedTuple<String> highestLevelTuple = result.iterator().next();
        if (highestLevelTuple.getScore() == null || highestLevelTuple.getValue() == null) {
            return null;
        }

        int level = Integer.parseInt(highestLevelTuple.getValue());
        double exp = highestLevelTuple.getScore();

        return new LevelExpInfo(level, exp);
    }


    /**
     * 레벨에 따른 요구 최종 경험치
     * @param level
     * @return
     */
    public Double getExpByLevel(int level) {
        return zSetOperations.score(EXP_KEY, String.valueOf(level));
    }

    public void initializeLevelExpTable() {

        // 이미 저장된 경우 생략
        if (Boolean.TRUE.equals(template.hasKey(EXP_KEY))) {
            return;
        }

        double baseExp = 100;
        double multiplier = 1.09;
        int maxLevel = 100;
        double pSum = 0;

        zSetOperations.add(EXP_KEY, "1", 0);

        for (int level = 2; level <= maxLevel; level++) {
            double requiredExp = Math.ceil(baseExp * Math.pow(multiplier, level - 2));
            pSum += requiredExp;

            zSetOperations.add(EXP_KEY, String.valueOf(level), pSum);
        }
    }
}
