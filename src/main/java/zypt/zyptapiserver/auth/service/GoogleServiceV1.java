package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.GoogleUserInfo;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.util.JwtUtils;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class GoogleServiceV1 implements SocialService {

    private final String client_id;
    private final ObjectMapper objectMapper;
    private final OIDCService service;
    private final JwtUtils jwtUtils;


    // 유저 정보 가져오기
    @Override
    public UserInfo getUserInfo(String token) {
        String[] header = token.split("\\.");
        byte[] decode = Base64.getUrlDecoder().decode(header[0]);

        try {
            JsonNode node = objectMapper.readTree(decode);
            log.info("node = {}", node.toPrettyString());
            String kid = node.get("kid").asText();

            OIDCPublicKeyDto keyDto = service.getPublicKeyByKid(kid, SocialType.GOOGLE);
            Claims claims = jwtUtils.validationIdToken(token, client_id, SocialType.GOOGLE.getIss(), keyDto);

            return new GoogleUserInfo(claims.getSubject(), claims.get("email", String.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
