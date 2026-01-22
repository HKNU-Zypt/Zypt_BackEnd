package zypt.zyptapiserver.aop.oidc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import zypt.zyptapiserver.auth.service.KakaoService;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.DistributedLockException;
import zypt.zyptapiserver.exception.InvalidOidcPublicKeyException;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OIDCAspectTest {

    @Mock
    CacheManager cacheManager;

    @Mock
    RedissonClient redissonClient;

    @Mock
    StringRedisTemplate redis;

    @Mock
    ProceedingJoinPoint joinPoint;

    @Mock
    RLock lock;

    OIDCAspect aspect;

    @BeforeEach
    void init() {
        aspect = new OIDCAspect(cacheManager, redissonClient, redis);
    }

    @Mock
    MockSignature mockSignature;
    static class MockSignature implements Signature {

        @Override
        public String toShortString() {
            return "";
        }

        @Override
        public String toLongString() {
            return "";
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public int getModifiers() {
            return 0;
        }

        @Override
        public Class getDeclaringType() {
            return null;
        }

        @Override
        public String getDeclaringTypeName() {
            return "";
        }
    }

    @Test
    @DisplayName("직접 목록키 갱신 성공")
    void keyExceptionRetrySuccess() throws Throwable {
        SocialType type = SocialType.KAKAO;
        when(redissonClient.getLock(anyString())).thenReturn(lock);

        when(lock.tryLock(5,-1, TimeUnit.SECONDS)).thenReturn(true);

        when(redis.hasKey("oidc:public-keys:refreshed:" + type.name())).thenReturn(false);


        when(joinPoint.getSignature()).thenReturn(mockSignature);
        when(mockSignature.getDeclaringType()).thenReturn(KakaoService.class);

        when(joinPoint.proceed())
                .thenThrow(new InvalidOidcPublicKeyException("검증 실패"))
                .thenReturn(new UserInfo("test", "email"));

        when(lock.isHeldByCurrentThread()).thenReturn(true);


        UserInfo result = aspect.oidcReCache(joinPoint);

        verify(joinPoint, times(2)).proceed();
        verify(lock, times(1)).isHeldByCurrentThread();
        verify(lock, times(1)).unlock();
        assertNotNull(result);


        Assertions.assertThat(result.getId()).isEqualTo("test");
        Assertions.assertThat(result.getEmail()).isEqualTo("email");

    }

    @Test
    @DisplayName("목록키 변경으로 재시도 진입했지만 락 잡기전에 다른 쓰레드가 마킹 성공")
    void keyExceptionDifferenceThreadMarkingSuccess() throws Throwable {
        SocialType type = SocialType.KAKAO;

        when(redis.hasKey("oidc:public-keys:refreshed:" + type.name())).thenReturn(true);

        when(joinPoint.proceed())
                .thenReturn(new UserInfo("test", "email"));

        UserInfo result = aspect.proceedWithLock(joinPoint, type);

        verify(joinPoint, times(1)).proceed();
        verify(lock, times(0)).isHeldByCurrentThread();
        verify(lock, times(0)).unlock();
        assertNotNull(result);


        Assertions.assertThat(result.getId()).isEqualTo("test");
        Assertions.assertThat(result.getEmail()).isEqualTo("email");

    }
    @Test
    @DisplayName("락 잡고 들어갔는데 다른 쓰레드가 마킹")
    void keyExceptionMarkingSuccess() throws Throwable {
        SocialType type = SocialType.KAKAO;

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(redis.hasKey("oidc:public-keys:refreshed:" + type.name()))
                .thenReturn(false)
                .thenReturn(true);

        when(lock.tryLock(5,-1, TimeUnit.SECONDS)).thenReturn(true);

        when(joinPoint.proceed())
                .thenReturn(new UserInfo("test", "email"));

        when(lock.isHeldByCurrentThread()).thenReturn(true);
        UserInfo result = aspect.proceedWithLock(joinPoint, type);

        verify(joinPoint, times(1)).proceed();
        verify(lock, times(1)).isHeldByCurrentThread();
        verify(lock, times(1)).unlock();
        assertNotNull(result);


        Assertions.assertThat(result.getId()).isEqualTo("test");
        Assertions.assertThat(result.getEmail()).isEqualTo("email");

    }
    @Test
    @DisplayName("락 점유 실패후 다른 쓰레드가 마킹하여 성공")
    void keyExceptionLockFailAndMarkingSuccess() throws Throwable {
        SocialType type = SocialType.KAKAO;

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(redis.hasKey("oidc:public-keys:refreshed:" + type.name()))
                .thenReturn(false)
                .thenReturn(true);

        when(lock.tryLock(5,-1, TimeUnit.SECONDS)).thenReturn(false);

        when(joinPoint.proceed())
                .thenReturn(new UserInfo("test", "email"));

        UserInfo result = aspect.proceedWithLock(joinPoint, type);

        verify(joinPoint, times(1)).proceed();
        assertNotNull(result);


        Assertions.assertThat(result.getId()).isEqualTo("test");
        Assertions.assertThat(result.getEmail()).isEqualTo("email");

    }

    @Test
    @DisplayName("락 점유 실패후 다른 쓰레드도 마킹 실패")
    void keyExceptionLockFailAndMarkingFail() throws Throwable {
        SocialType type = SocialType.KAKAO;

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(redis.hasKey("oidc:public-keys:refreshed:" + type.name()))
                .thenReturn(false);

        when(lock.tryLock(5,-1, TimeUnit.SECONDS)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> aspect.proceedWithLock(joinPoint, type))
                .isInstanceOf(DistributedLockException.class);

    }
}