package fstt.fsttapiserver.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fstt.fsttapiserver.auth.user.KakaoUserInfo;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class KakaoService {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 유저 정보 가져오기
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode json = objectMapper.readTree(response.getBody());

                JsonNode profile = json.get("profile");

                return new KakaoUserInfo(json.get("id").asText()
                        ,profile.get("nickname").asText()
                        ,profile.get("profile_image_url").asText());

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
}
