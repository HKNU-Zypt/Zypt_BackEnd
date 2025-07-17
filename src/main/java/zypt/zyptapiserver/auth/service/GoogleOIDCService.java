package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.annotation.SocialIdentifier;
import zypt.zyptapiserver.auth.exception.InvalidTokenException;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeysDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.GoogleUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.util.JwtUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
@SocialIdentifier(SocialType.GOOGLE)
@RequiredArgsConstructor
public class GoogleOIDCService implements SocialService {

    private final String client_id;
    private final ObjectMapper objectMapper;
    private final OIDCService service;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate;

    // 유저 정보 가져오기
    @Override
    public UserInfo getUserInfo(String token) {
        String[] header = token.split("\\.");

        byte[] decode = Base64.getUrlDecoder().decode(header[0]);


        try {
            JsonNode node = objectMapper.readTree(decode);
            log.info("node = {}", node.toPrettyString());
            String kid = node.get("kid").asText();


            String jwksUrl = service.getJwksUrl(SocialType.GOOGLE);
            OIDCPublicKeysDto publicKeysDto = service.getOpenIdPublicKeys(SocialType.GOOGLE, jwksUrl);
            OIDCPublicKeyDto keyDto = service.getPublicKeyByKid(kid, publicKeysDto.keys()); // kid에 맞는 공개키 탐색
            PublicKey key = OIDCService.createRsaPublicKey(keyDto);
            Claims claims = jwtUtils.validationIdToken(token, client_id, SocialType.GOOGLE.getIss(), key);
            return new GoogleUserInfo(claims.getSubject(), claims.get("email", String.class));
        } catch (IOException e) {
            throw new IllegalStateException("ID 토큰 헤더 파싱에 실패했습니다. ",e);
        }
    }

    @Override
    public void disconnectSocialAccount(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("token", accessToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(multiValueMap,httpHeaders);
        ResponseEntity<String> res = restTemplate.postForEntity("https://oauth2.googleapis.com/revoke", entity, String.class);

        if (res.getStatusCode() != HttpStatus.OK) {
            throw new InvalidTokenException("요청 실패 구글 액세스토큰이 정상적이지 않거나 잘못된 요청입니다. ");
        }

        log.info("구글 연동 해제 완료");

    }
}
