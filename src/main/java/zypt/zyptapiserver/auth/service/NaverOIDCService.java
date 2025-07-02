package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import zypt.zyptapiserver.auth.exception.JsonCustomException;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeysDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.NaverUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.dto.NaverRefreshAccessTokenDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.util.JwtUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
@SocialIdentifier(SocialType.NAVER)
@RequiredArgsConstructor
public class NaverOIDCService implements SocialService {
    private final String naverClientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper;
    private final OIDCService service;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate;

    private static final String NAVER_TOKEN_REQUEST_URL = "https://nid.naver.com/oauth2.0/token";


    // 유저 정보 가져오기
    @Override
    public UserInfo getUserInfo(String token)  {
        // 토큰에서 kid를 공개키 목록에 있는지 확인 후 공개키 정보를 가져옴
        String header = token.split("\\.")[0];
        // ID 토큰 헤더 디코딩
        byte[] decode = Base64.getUrlDecoder().decode(header);

        try {
            JsonNode jsonNode = objectMapper.readTree(decode);
            String kid = jsonNode.get("kid").asText(); // kid 추출

            String jwksUrl = service.getJwksUrl(SocialType.NAVER);
            OIDCPublicKeysDto publicKeysDto = service.getOpenIdPublicKeys(SocialType.NAVER, jwksUrl);

            OIDCPublicKeyDto keyDto = service.getPublicKeyByKid(kid, publicKeysDto.keys()); // kid에 맞는 공개키 탐색
            PublicKey key = OIDCService.createRsaPublicKey(keyDto);

            // 검증
            Claims claims = jwtUtils.validationIdToken(token, naverClientId, SocialType.NAVER.getIss(), key);

            return new NaverUserInfo(claims.getSubject(), "tmp");

        } catch (IOException e) {
            throw new IllegalStateException("ID 토큰 헤더 파싱에 실패했습니다. " , e);
        }

    }

    @Override
    public void disconnectSocialAccount(String refreshToken) {

        log.info("네이버 리프레시 토큰 = {}", refreshToken);

        // 액세스토큰 갱신 요청
        ResponseEntity<NaverRefreshAccessTokenDto> accessResponse =
                restTemplate.postForEntity(
                        NAVER_TOKEN_REQUEST_URL,
                        getRefreshMultiValueMap(refreshToken),
                        NaverRefreshAccessTokenDto.class
                );

        if (accessResponse.getStatusCode() != HttpStatus.OK || accessResponse.getBody().access_token() == null) {
            throw new IllegalArgumentException("존재하지 않는 리프레시 토큰");
        }

        String accessToken = accessResponse.getBody().access_token();
        log.info("네이버 액세스토큰 재발급 완료 {}", accessToken);

        // 소셜 연동 해제 요청
        ResponseEntity<String> res =
                restTemplate.postForEntity(
                        NAVER_TOKEN_REQUEST_URL,
                        getdeleteMultiValueMap(accessToken),
                        String.class
                );
        if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null ) {
            throw new RestClientException("소셜 연동 해제 실패");
        }

        log.info("response = code : {}, body = {}", res.getStatusCode() , res.getBody());
        try {
            String result = objectMapper.readTree(res.getBody()).get("result").asText();
            if (result.equals("success")) {
                log.info("소셜 연동 해제 완료");

            } else {
                throw new IllegalStateException("소셜 연동 해제 실패");
            }

        }  catch (JsonMappingException e) {
            throw new JsonCustomException("소셜 연동 해제 요청 응답 파싱후 매핑 실패 ",e);
        } catch (JsonProcessingException e) {
            throw new JsonCustomException("소셜 연동 해제 요청 응답 파싱 진행 실패 ",e);
        }
    }

    private MultiValueMap<String, String> getRefreshMultiValueMap(String refreshToken) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("grant_type", "refresh_token");
        multiValueMap.add("client_id", naverClientId);
        multiValueMap.add("client_secret", clientSecret);
        multiValueMap.add("refresh_token", refreshToken);
        return multiValueMap;
    }

    private MultiValueMap<String, String> getdeleteMultiValueMap(String accessToken) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("grant_type", "delete");
        multiValueMap.add("client_id", naverClientId);
        multiValueMap.add("client_secret", clientSecret);
        multiValueMap.add("access_token", accessToken);
        return multiValueMap;
    }

}
