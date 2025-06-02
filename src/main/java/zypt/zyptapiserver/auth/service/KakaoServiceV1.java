package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.KakaoUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.util.JwtUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
@EqualsAndHashCode
public class KakaoServiceV1 implements SocialService {

    private String kakaoAppKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OIDCService service = new OIDCService();

    private final JwtUtils jwtUtils;
    public KakaoServiceV1(String kakaoAppKey, JwtUtils jwtUtils) {
        this.kakaoAppKey = kakaoAppKey;
        this.jwtUtils = jwtUtils;
    }

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

            OIDCPublicKeyDto keyDto = service.getPublicKeyByKid(kid, SocialType.KAKAO); // kid에 맞는 공개키 탐색
            // 검증
            Claims claims = jwtUtils.validationIdToken(token, null, keyDto);

            return new KakaoUserInfo(claims.getSubject(),
                    claims.get("email", String.class));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
