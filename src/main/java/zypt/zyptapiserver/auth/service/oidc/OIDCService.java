package zypt.zyptapiserver.auth.service.oidc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class OIDCService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();


    // TODO advisor로 예외 터졌을 시 캐시 갱신하도록 짜야함  CacheManager 이용
    // OIDC 문서 정보 가져오기
    // SpringEL언어 사용
    @Cacheable(value = "OIDC_JWKS", key = "#socialType")
    public String getJwksUrl(SocialType socialType)  {

        String url = switch (socialType) {
            case KAKAO -> "https://kauth.kakao.com/.well-known/openid-configuration";
            case GOOGLE -> "https://accounts.google.com/.well-known/openid-configuration";
            case NAVER -> "https://nid.naver.com/.well-known/openid-configuration";
        };

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            JsonNode jsonNode = mapper.readTree(response.getBody());
            return jsonNode.get("jwks_uri").asText();

        } catch (JsonProcessingException e) {
            //TODO 예외 따로?
            throw new RuntimeException(e);
        }

    }


    /**
     * 캐시전략 사용
     * 공개키를 획득
     * @return
     */
    @Cacheable(value = "OCIDPublicKeys", key = "#socialType + '_keys'")
    public OIDCPublicKeysDto getOpenIdPublicKeys(SocialType socialType, String jwksUrl){
        ResponseEntity<OIDCPublicKeysDto> response = restTemplate.getForEntity(
                jwksUrl,
                OIDCPublicKeysDto.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("공개키 획득 실패");
        }

        log.info("body = {}", Arrays.toString(response.getBody().keys()));
        return response.getBody();
    }

    /**
     *  kid : 공개키 id로  공개키 목록중 맞는 걸 찾아서
     *  반환한다.
     * @param kid
     * @return
     */
    public OIDCPublicKeyDto getPublicKeyByKid(String kid, OIDCPublicKeyDto[] keys) {
        return Arrays.stream(keys)
                .filter(k -> k.kid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 공개키가 없습니다."));
    }

    /**
     * 주어진 공개키 정보로 공개키를 조합해서 생성후 반환한다.
     * @param dto
     * @return
     */
    public static PublicKey createRsaPublicKey(OIDCPublicKeyDto dto) {
        BigInteger moduls = new BigInteger(1, Base64.getUrlDecoder().decode(dto.n()));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(dto.e()));
        RSAPublicKeySpec spec = new RSAPublicKeySpec(moduls, exponent); // RSA 공개키 스펙(형태) 객체

        try {
            // key 타입은 RSA이며
            // RSA256은 RSA 키 타입 + SHA-256을 사용하는 방식
            return KeyFactory.getInstance("RSA").generatePublic(spec);

            // TODO 예외 처리?
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
