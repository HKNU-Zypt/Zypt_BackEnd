package zypt.zyptapiserver.auth.service.oidc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Slf4j
public class OIDCService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 캐시전략 사용
     * 공개키를 획득
     * @return
     */
    @Cacheable(value = "OCIDPublicKeys", key = "'kakao'")
    public OIDCPublicKeysDto getOpenIdPublicKeys() {

        ResponseEntity<OIDCPublicKeysDto> response = restTemplate.exchange(
                "https://kauth.kakao.com/.well-known/jwks.json",
                HttpMethod.GET,
                null,
                OIDCPublicKeysDto.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("공개키 획득 실패");
        }

        log.info("body = {}", Arrays.toString(response.getBody().keys()));
        return response.getBody();
    }

    /**
     *  OIDC 공개키로 id Token 검증만 해줌
     * @param kid
     * @return
     */
    public OIDCPublicKeyDto getPublicKeyByKid(String kid) {
        OIDCPublicKeyDto[] keys = getOpenIdPublicKeys().keys();

        return Arrays.stream(keys)
                .filter(k -> k.kid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 공개키가 없습니다."));
    }


}
