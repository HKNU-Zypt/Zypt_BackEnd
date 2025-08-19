package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.annotation.SocialIdentifier;
import zypt.zyptapiserver.exception.JsonCustomException;
import zypt.zyptapiserver.exception.MissingTokenException;
import zypt.zyptapiserver.auth.user.NaverUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.dto.member.NaverRefreshAccessTokenDto;
import zypt.zyptapiserver.domain.enums.SocialType;

@Slf4j
@Service
@SocialIdentifier(SocialType.NAVER)
@RequiredArgsConstructor
public class NaverService implements SocialService {

    @Value("${naver.CLIENT_ID}")
    private String naverClientId;
    @Value("${naver.CLIENT_SECRET}")
    private String clientSecret;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String NAVER_TOKEN_REQUEST_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";
    // 유저 정보 가져오기
    @Override
    public UserInfo getUserInfo(String token)  {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(
                    PROFILE_URL,
                    requestEntity,
                    String.class);

        } catch (RestClientException ex) {
            throw new MissingTokenException("잘못되거나 만료된 토큰");

        }

        try {
            JsonNode profile = objectMapper.readTree(response.getBody());
            String socialId = profile.get("response").get("id").asText();
            String email = profile.get("response").get("email").asText();


            return new NaverUserInfo(socialId, email);

        } catch (JsonProcessingException e) {
            throw new JsonCustomException(e);
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
