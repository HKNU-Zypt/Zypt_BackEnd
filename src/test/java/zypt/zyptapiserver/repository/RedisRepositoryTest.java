package zypt.zyptapiserver.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisRepositoryTest {

    @Autowired
    RedisRepository redisRepository;

    @Test
    @DisplayName("redis 캐시에 없을때 delete 요청 테스트")
    void deleteRedisToken() {
        Assertions.assertThatCode(()-> redisRepository.deleteRefreshToken("abc")).isNull();
    }
}