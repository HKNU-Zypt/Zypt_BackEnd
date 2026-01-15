package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeysDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.exception.InvalidOidcPublicKeyException;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import static org.mockito.Mockito.*;


@Slf4j
@SpringBootTest
class SocialServiceTest {
    @Value("${kakao.APP_KEY}")
    private String kakaoAppKey;
    @Autowired
    SocialServiceFactory socialServiceFactory;

    @MockitoBean
    OIDCService mockOidcService;

    @Autowired
    OIDCService oidcService;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    ObjectMapper mapper;


    @Test
    @DisplayName("SocialService AOP 적용 테스트")
    void oidcAopTest() throws Exception {
        OIDCPublicKeyDto dto = createTestOidcPublicKeyDto(generateTestKeyPair(), "a");

        //given
        when(mockOidcService.getJwksUrl(SocialType.KAKAO)).thenReturn("url");
        when(mockOidcService.getOpenIdPublicKeys(SocialType.KAKAO, "url")).thenReturn(new OIDCPublicKeysDto(new OIDCPublicKeyDto[]{dto}));
        when(mockOidcService.getPublicKeyByKid("a", new OIDCPublicKeyDto[]{dto})).thenReturn(dto);
        when(mockOidcService.createRsaPublicKey(dto)).thenReturn(createRsaPublicKey(dto));

        SocialService service = socialServiceFactory.getService(SocialType.KAKAO);

        //when && then
        try {
            service.getUserInfo("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImEifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTc2ODU1MTUyM30.j2DCpcmwP6CCruAz_QLplNbkjmAR3wdNQ4_g7aJBGsnWofIt23TXbJD7e89YssNzkxysMHUpChGnZnTO2_T_u1_MAsEGjH2d2qHqod-Hn4KUB-uZzz4tGpgIwcfx027khixvuFIgO-Ixb6JBXqoXptKexWZwoS0ov3Oq3Rr0LFHHY8KzNtTlZ9S6Bp-ZspKUVsT5GXCzfU2aZFhPfMDOCbyH8LfQY7Y72adM5Rn867zol9J-arG-z1A3UOCnvS591mXm_RrmkyGzjhroPRK1Yu_YfCelVolCBndcnhVVUzfKUvUxieXkD6GEiYrKEifyXuKzo4V1ztQ07o_mgUu1hA");

        } catch (InvalidOidcPublicKeyException e) {
            // AOP가  재시도 했는지 검증
            verify(mockOidcService, times(2)).getOpenIdPublicKeys(SocialType.KAKAO, "url");

        }
    }

    @Test
    @DisplayName("OIDC url이 변경되어 캐시 삭제 후 재시도 로직 테스트")
    void deleteOIDCRetryTest() throws Exception {
        //given
        KeyPair oldPair = generateTestKeyPair();
        KeyPair newPair = generateTestKeyPair();

        OIDCPublicKeyDto oldDto = createTestOidcPublicKeyDto(oldPair, "test-kid");
        OIDCPublicKeyDto newDto = createTestOidcPublicKeyDto(newPair, "new-kid");

        // 직접 new-kid로 갱신된 id토큰 생성
        String idToken = Jwts.builder()
                .setIssuer("https://kauth.kakao.com")
                .setAudience(kakaoAppKey)
                .setSubject("123") // id
                .setHeaderParam("kid", "new-kid")
                .signWith(newPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

        when(mockOidcService.getJwksUrl(SocialType.KAKAO)).thenReturn("url");
        when(mockOidcService.getOpenIdPublicKeys(SocialType.KAKAO, "url"))
                .thenReturn(new OIDCPublicKeysDto(new OIDCPublicKeyDto[]{oldDto})) // 처음 시도시 캐시된 oldDto
                .thenReturn(new OIDCPublicKeysDto(new OIDCPublicKeyDto[]{newDto})); // 두번째 시도시 캐시 삭제후 재발급한 newDto

        // 마찬가지로 1번 2번 시도마다 다르게 dto 반환
        when(mockOidcService.getPublicKeyByKid(Mockito.eq("new-kid"), any(OIDCPublicKeyDto[].class)))
                .thenReturn(oldDto)
                .thenReturn(newDto);

        when(mockOidcService.createRsaPublicKey(any(OIDCPublicKeyDto.class)))
                .thenReturn(createRsaPublicKey(oldDto))
                .thenReturn(createRsaPublicKey(newDto));

        SocialService service = socialServiceFactory.getService(SocialType.KAKAO);

        //when && then
        UserInfo userInfo = service.getUserInfo(idToken);
        verify(mockOidcService, times(2))
                .getOpenIdPublicKeys(SocialType.KAKAO, "url");

        Assertions.assertThat(userInfo.getId()).isEqualTo("123");
    }



    private static KeyPair generateTestKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static PublicKey createRsaPublicKey(OIDCPublicKeyDto dto) {
        BigInteger moduls = new BigInteger(1, Base64.getUrlDecoder().decode(dto.n()));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(dto.e()));
        RSAPublicKeySpec spec = new RSAPublicKeySpec(moduls, exponent); // RSA 공개키 스펙(형태) 객체

        try {
            // key 타입은 RSA이며
            // RSA256은 RSA 키 타입 + SHA-256을 사용하는 방식
            return KeyFactory.getInstance("RSA").generatePublic(spec);

            // TODO 예외 처리
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new InvalidOidcPublicKeyException("RSA 공개키 생성 실패", e);
        }
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