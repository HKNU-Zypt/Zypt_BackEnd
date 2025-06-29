package zypt.zyptapiserver.aop.oidc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.stereotype.Component;
import zypt.zyptapiserver.annotation.SocialIdentifier;
import zypt.zyptapiserver.auth.exception.InvalidOidcPublicKeyException;
import zypt.zyptapiserver.auth.exception.InvalidTokenException;
import zypt.zyptapiserver.auth.exception.OidcPublicKeyFetchException;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.Objects;


@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class OIDCAspect {

    private final CacheManager cacheManager;
//    private final OIDCService service = new OIDCService();

//    @Around("OIDCPointcut.oidc()")
//    public OIDCPublicKeyDto cache(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object[] param = joinPoint.getArgs();
//        SocialType socialType = (SocialType) param[0];
//
//        try {
//            joinPoint.proceed();
//
//        } catch (IllegalArgumentException e) {
//            cacheManager.getCache(socialType.name()).clear();
//            cacheManager.getCache(socialType.name() + "_keys").clear();
//            String jwksUrl = service.getJwksUrl(socialType);
//            OIDCPublicKeysDto publicKeys = service.getOpenIdPublicKeys(socialType, jwksUrl);
//
//        }
//        return null;
//    }

    @Around("OIDCPointcut.getUserInfo()")
    public UserInfo oidcReCache(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("OIDC를 통한 소셜 로그인 시작 ");
            return (UserInfo) joinPoint.proceed();

        } catch (OidcPublicKeyFetchException | InvalidTokenException  | InvalidOidcPublicKeyException e) {
            log.warn("SocialService 호출 실패로 캐시 초기화 후 재시도: {}", e.getMessage(), e);

            // social 타입을 정의한 SocialIdentifier 애노테이션으로 찾기
            Class<?> clazz = ((MethodSignature) joinPoint.getSignature()).getDeclaringType();
            SocialIdentifier annotation = clazz.getAnnotation(SocialIdentifier.class);
            SocialType type = annotation.value();

            // OIDC 문서 및 JWKS로 얻은 공개키 캐시를 초기화
            // requireNonNull : null -> NPE 던짐
            Cache jwkCache = cacheManager.getCache(type.name());
            if (jwkCache != null) {
                jwkCache.clear();
                log.info("oidc jwk 캐시 삭제 : type = {}", type);
            }

            Cache publicKeyCache = cacheManager.getCache(type.name() + "_keys");
            if (publicKeyCache != null) {
                publicKeyCache.clear();
                log.info("oidc publicKey목록 캐시 삭제 : type = {}", type);
            }

            // 재실행
            return (UserInfo) joinPoint.proceed();
        }
    }


}
