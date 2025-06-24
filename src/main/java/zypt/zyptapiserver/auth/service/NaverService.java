package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zypt.zyptapiserver.annotation.SocialIdentifier;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeysDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.KakaoUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.util.JwtUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
@SocialIdentifier(SocialType.NAVER)
@RequiredArgsConstructor
public class NaverService implements SocialService {
    private final String naverClientId;
    private final ObjectMapper objectMapper;
    private final OIDCService service;
    private final JwtUtils jwtUtils;

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

            return new KakaoUserInfo(claims.getSubject(), "tmp");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean disconnectSocialAccount(String accessToken) {
        return false;
    }

}
