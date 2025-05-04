package zypt.zyptapiserver.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final StringRedisTemplate template;

    public void saveRefreshToken(String memberId, String refreshToken) {
        valueOperations().set("refresh:" + memberId, refreshToken, Duration.ofDays(14));
    }

    // 없을 시 null 반환, null when key does not exist or used in pipeline / transaction.
    public String findRefreshToken(String memberId) {
        return valueOperations().get("refresh:" + memberId);
    }

    private ValueOperations<String, String> valueOperations() {
        return template.opsForValue();
    }




}
