package zypt.zyptapiserver.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import zypt.zyptapiserver.domain.exp.LevelExpInfo;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RedisCacheRepositoryTest {


    RedisCacheRepository repository;

    ZSetOperations<String, String> zSetOperations;

    StringRedisTemplate redisTemplate;

    @Autowired
    public RedisCacheRepositoryTest(RedisCacheRepository repository, @Qualifier("cacheRedisTemplate") StringRedisTemplate redisTemplate) {
        this.zSetOperations = redisTemplate.opsForZSet();
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }



    @BeforeEach
    void init() {
        repository.initializeLevelExpTable();
    }

    @Test
    void zSetTest() {
        LevelExpInfo test1 = repository.getLevelExpInfoByExp(444);

        Double expByLevel = repository.getExpByLevel(54);
        LevelExpInfo test2 = repository.getLevelExpInfoByExp(expByLevel - 100);


        log.info(test1.toString());
        log.info(test2.toString());

        Assertions.assertThat(test1.level()).isEqualTo(4);
        Assertions.assertThat(test2.level()).isEqualTo(53);
    }

    @Test
    void zSetLimitTest() {
        Set<ZSetOperations.TypedTuple<String>> expTable =
                zSetOperations.reverseRangeByScoreWithScores("EXP_TABLE", 0, 1523, 0, 1);

        Assertions.assertThat(expTable.size()).isEqualTo(1);

        Set<ZSetOperations.TypedTuple<String>> expTable1 = zSetOperations.reverseRangeByScoreWithScores("EXP_TABLE", 0, 1523);
        log.info("limit 안걸고 조회시 개수 = {}", expTable1.size());
        Assertions.assertThat(expTable1).isNotEqualTo(1);
    }

    // used memory 94MB
    // time 4128 ms
    @Test
    void performanceTest() {
        long s = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            Set<ZSetOperations.TypedTuple<String>> expTable = zSetOperations.reverseRangeByScoreWithScores("EXP_TABLE", 0, 100000);
        }
        long e = System.currentTimeMillis();
        checkMemory();

        log.info("time = {}", e - s);
    }

    // used memory 62 MB
    // time 3329 ms
    @Test
    void performance2Test() {
        long s = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Set<ZSetOperations.TypedTuple<String>> expTable = zSetOperations.reverseRangeByScoreWithScores("EXP_TABLE", 0, 100000, 0, 1);
        }
        long e = System.currentTimeMillis();
        checkMemory();

        log.info("time = {}", e - s);
    }

    public void checkMemory() {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();     // JVM이 사용할 수 있는 최대 메모리
        long totalMemory = runtime.totalMemory(); // 현재 JVM에 할당된 총 메모리
        long freeMemory = runtime.freeMemory();   // 할당된 메모리 중 여유 공간
        long usedMemory = totalMemory - freeMemory; // 실제 사용 중인 메모리

        log.info("Used Memory: {} MB", usedMemory / (1024 * 1024));
    }
}