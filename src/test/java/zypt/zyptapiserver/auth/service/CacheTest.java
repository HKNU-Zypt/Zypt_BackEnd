package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeysDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.mockito.Mockito.*;

@SpringBootTest
public class CacheTest {

    @Autowired
    SocialServiceFactory socialServiceFactory;

    @Autowired
    OIDCService oidcService;

    @Autowired
    CacheManager cacheManager;

    @MockitoBean
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper mapper;
    @BeforeEach
    void clearCache() {
        cacheManager.getCache("OIDCPublicKeys").clear();
        cacheManager.getCache("OIDC_JWKS").clear();
    }

    @Test
    void cacheTest() throws Exception {
        KeyPair pair1 = generateTestKeyPair();
        KeyPair pair2 = generateTestKeyPair();
        OIDCPublicKeyDto dto1 = createTestOidcPublicKeyDto(pair1, "kid-1");
        OIDCPublicKeyDto dto2 = createTestOidcPublicKeyDto(pair2, "kid-2");

        OIDCPublicKeysDto keys1 = new OIDCPublicKeysDto(new OIDCPublicKeyDto[]{dto1});
        OIDCPublicKeysDto keys2 = new OIDCPublicKeysDto(new OIDCPublicKeyDto[]{dto2});

        String jwkUrl = "https://abc.json";


        // oidcService에서 getOpenIdPublicKeys를 호출시 아래의 요청이 나감
        when(restTemplate.getForEntity(jwkUrl, OIDCPublicKeysDto.class))
                .thenReturn(ResponseEntity.ok(keys1))
                .thenReturn(ResponseEntity.ok(keys2));

        OIDCPublicKeysDto r1 = oidcService.getOpenIdPublicKeys(SocialType.KAKAO, jwkUrl);
        OIDCPublicKeysDto r2 = oidcService.getOpenIdPublicKeys(SocialType.KAKAO, jwkUrl);

        // 캐시 적용됐는지 확인
        verify(restTemplate, times(1))
                .getForEntity(jwkUrl, OIDCPublicKeysDto.class);

        Cache cache = cacheManager.getCache("OIDCPublicKeys");
        Assertions.assertNotNull(cache);

        // 캐시 삭제
        cache.evict(SocialType.KAKAO.name() + "_keys");

        // 다시 재 요청시 총 2번 호출되는지 확인
        OIDCPublicKeysDto r3 = oidcService.getOpenIdPublicKeys(SocialType.KAKAO, jwkUrl);
        verify(restTemplate, times(2))
                .getForEntity(jwkUrl, OIDCPublicKeysDto.class);


        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
        Assertions.assertEquals(1, r1.keys().length);
        Assertions.assertEquals("kid-1", r1.keys()[0].kid());
        Assertions.assertEquals("kid-2", r3.keys()[0].kid());
    }




    private static KeyPair generateTestKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }


    private static OIDCPublicKeyDto createTestOidcPublicKeyDto(KeyPair keyPair, String kid) throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        String n = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(publicKey.getModulus().toByteArray());

        String e = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(publicKey.getPublicExponent().toByteArray());

        return new OIDCPublicKeyDto(
                kid,
                "RSA",      // kty
                "RS256",    // alg
                "sig",      // use
                n,
                e
        );
    }
}
