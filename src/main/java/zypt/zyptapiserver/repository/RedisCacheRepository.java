package zypt.zyptapiserver.repository;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;

import java.util.Set;

public class RedisCacheRepository {

    private static final String EXP_KEY = "exp_table";
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
        Set<ZSetOperations.TypedTuple<String>> result
                = zSetOperations.reverseRangeByScoreWithScores(EXP_KEY, 0, curExp);

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
}
