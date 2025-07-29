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
public class  RedisRepository {

    private final StringRedisTemplate template;

    public void saveRefreshToken(String memberId, String refreshToken) {
        valueOperations().set("refresh:" + memberId, refreshToken, Duration.ofDays(14));
    }

    // 없을 시 null 반환
    public String findRefreshToken(String memberId) {
        return valueOperations().get("refresh:" + memberId);
    }

    /**
     * 삭제, 없을 시 false 반환하지만 굳이 할 필요없음
     * @param memberId
     */
    public void deleteRefreshToken(String memberId) {
        template.delete("refresh:" + memberId);
        log.info("refreshToken 삭제 = {}", memberId);
    }

    private ValueOperations<String, String> valueOperations() {
        return template.opsForValue();
    }

}
