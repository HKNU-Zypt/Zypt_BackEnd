package zypt.zyptapiserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.auth.service.*;
import zypt.zyptapiserver.auth.service.oidc.OIDCService;
import zypt.zyptapiserver.domain.enums.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zypt.zyptapiserver.util.JwtUtils;

import java.util.EnumMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SocialServiceConfig {

    @Value("${google.CLIENT_ID}")
    private String clientId;

    @Value("${kakao.APP_KEY}")
    private String kakaoAppKey;

    private final OIDCService service = new OIDCService();
    private final ObjectMapper mapper;
    private final JwtUtils jwtUtils;

    // 소셜 타입 , 소셜 서비스를 맵에 등록
    public Map<SocialType, SocialService> socialTypeSocialServiceMap() {
        Map<SocialType, SocialService> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, getKakaoServiceV1());
        map.put(SocialType.GOOGLE, getGoogleServiceV1());
        map.put(SocialType.NAVER, null);
        return map;
    }

    private @NotNull GoogleServiceV1 getGoogleServiceV1() {
        return new GoogleServiceV1(clientId, mapper, service, jwtUtils);
    }

    private @NotNull KakaoServiceV1 getKakaoServiceV1() {
        return new KakaoServiceV1(kakaoAppKey, mapper, service, jwtUtils);
    }

    @Bean
    public SocialServiceFactory socialServiceFactory() {
        return new SocialServiceFactory(socialTypeSocialServiceMap());
    }

}
