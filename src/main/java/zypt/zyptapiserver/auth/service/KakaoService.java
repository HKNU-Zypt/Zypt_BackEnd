package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import zypt.zyptapiserver.auth.user.KakaoUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@EqualsAndHashCode
public class KakaoService implements SocialService {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 유저 정보 가져오기
    @Override
    public UserInfo getUserInfo(String token) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 응답 성공시 카카오의 정보를 가져와 kakaoUserInfo 객체 생성해서 반환
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode json = objectMapper.readTree(response.getBody());
                JsonNode kakaoAccount = json.get("kakao_account");

                return new KakaoUserInfo(json.get("id").asText()
                        ,kakaoAccount.get("email").asText()
                );

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
}
