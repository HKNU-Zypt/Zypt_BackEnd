package zypt.zyptapiserver.aop.oidc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import zypt.zyptapiserver.annotation.SocialIdentifier;
import zypt.zyptapiserver.exception.*;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.repository.RedisCacheRepository;

import java.util.concurrent.TimeUnit;


@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class OIDCAspect {

    private static final String OIDC_JWKS_PREFIX = "OIDC_JWKS";
    private static final String OIDC_PREFIX = "OIDCPublicKeys";

    private final CacheManager cacheManager;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate cacheRedisTemplate;


    @Around("OIDCPointcut.getUserInfo()")
    public UserInfo oidcReCache(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("OIDC를 통한 소셜 로그인 시작 ");
        // social 타입을 정의한 SocialIdentifier 애노테이션으로 찾기
        SocialType type = getSocialType(joinPoint);

        try {
            return (UserInfo) joinPoint.proceed();

        } catch (OidcPublicKeyFetchException | InvalidOidcPublicKeyException e) {
            log.warn("SocialService 호출 실패로 캐시 초기화 후 재시도: {}", e.getMessage(), e);
            return proceedWithLock(joinPoint, type);
        }

    }

    // 락 걸고 OIDC 목록키 캐시 갱신 프로세스
    public UserInfo proceedWithLock(ProceedingJoinPoint joinPoint, SocialType type) throws Throwable {
        // 락 이름 생성
        String markerKey = "oidc:public-keys:refreshed:" + type.name();
        String lockKey = "lock:oidc:" + type.name();

        // 갱신 마커 확인
        if (Boolean.TRUE.equals(cacheRedisTemplate.hasKey(markerKey))) {
            return (UserInfo) joinPoint.proceed();
        }

        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock(5,-1, TimeUnit.SECONDS)) {
            try {

                // 다른 스레드가 갱신하고 마크 세웠는지 재확인
                if (!Boolean.TRUE.equals(cacheRedisTemplate.hasKey(markerKey))) {
                    evictOidcCache(type); // 캐시 삭제
                }

                return (UserInfo) joinPoint.proceed();

            } catch (Exception e) {
                // 재시도 중 발생하는 에러는 인증 실패 예외로 전환
                log.error("OIDC 재시도 중 최종 실패: type = {}, error = {}", type, e.getMessage());
                throw new OidcAuthenticationException("인증 서버와의 통신에 실패했습니다.", e);

            } finally {
                // 본인이 잡은 락이면 해제
                if (lock.isHeldByCurrentThread()) {
                    log.info("OIDC 락 해제 완료: type = {}", type.name());
                    lock.unlock();
                }
            }

        } else {
            // 락 획득 실패했지만 갱신 마크가 되어있다면 그대로 진행
            if (Boolean.TRUE.equals(cacheRedisTemplate.hasKey(markerKey))) {
                return (UserInfo) joinPoint.proceed();
            }

            throw new DistributedLockException("락 획득 실패");
        }
    }

    private void evictOidcCache(SocialType type) {
        // OIDC 문서 및 JWKS로 얻은 공개키 캐시를 초기화
        // requireNonNull : null -> NPE 던짐
        Cache jwkCache = cacheManager.getCache(OIDC_JWKS_PREFIX);
        if (jwkCache != null) {
            jwkCache.evict(type.name());
            log.info("oidc jwk 캐시 삭제 : type = {}", type);
        }

        Cache publicKeyCache = cacheManager.getCache(OIDC_PREFIX);
        if (publicKeyCache != null) {
            publicKeyCache.evict(type.name());
            log.info("oidc publicKey목록 캐시 삭제 : type = {}", type);
        }
    }

    private static SocialType getSocialType(ProceedingJoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();
        SocialIdentifier annotation = clazz.getAnnotation(SocialIdentifier.class);
        SocialType type = annotation.value();
        return type;
    }


}
