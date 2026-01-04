package zypt.zyptapiserver.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.annotation.SocialIdentifier;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeyDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCPublicKeysDto;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.dto.member.UnlinkDto;
import zypt.zyptapiserver.domain.enums.SocialType;
import zypt.zyptapiserver.util.JwtUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
@Service
@SocialIdentifier(SocialType.KAKAO)
@RequiredArgsConstructor
public class KakaoService implements SocialService {

    @Value("${kakao.APP_KEY}")
    private String kakaoAppKey;
    @Value("${kakao.ADMIN_KEY}")
    private String adminKey;

    private final ObjectMapper objectMapper;
    private final OIDCService service;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate;


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

            String jwksUrl = service.getJwksUrl(SocialType.KAKAO);
            OIDCPublicKeysDto publicKeysDto = service.getOpenIdPublicKeys(SocialType.KAKAO, jwksUrl);
            OIDCPublicKeyDto keyDto = service.getPublicKeyByKid(kid, publicKeysDto.keys()); // kid에 맞는 공개키 탐색
            PublicKey key = OIDCService.createRsaPublicKey(keyDto);

            // 검증
            Claims claims = jwtUtils.validationIdToken(token, kakaoAppKey, SocialType.KAKAO.getIss(), key);

            return new UserInfo(claims.getSubject(),
                    claims.get("email", String.class));

        } catch (IOException e) {
            throw new IllegalStateException("ID 토큰 헤더 파싱에 실패했습니다. ",e);
        }

    }

    @Override
    public void disconnectSocialAccount(String socialId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", socialId);


        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<UnlinkDto> response = restTemplate.postForEntity(
                SocialType.KAKAO.getUnlink(),
                entity,
                UnlinkDto.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("언링크 성공, 회원 번호 = {}", response.getBody());
        }
    }


}
