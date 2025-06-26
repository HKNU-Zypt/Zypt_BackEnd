package zypt.zyptapiserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Value("${kakao.ADMIN_KEY}")
    private String kakaoAdminKey;

    @Value("${naver.CLIENT_ID}")
    private String naverClientId;

    @Value("${naver.CLIENT_SECRET}")
    private String naverClientSecret;

    private final JwtUtils jwtUtils;
    private final ObjectMapper mapper;
    private final OIDCService service;

    private final RestTemplate restTemplate;

    // 소셜 타입 , 소셜 서비스를 맵에 등록
    public Map<SocialType, SocialService> socialTypeSocialServiceMap() {
        Map<SocialType, SocialService> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, getKakaoService());
        map.put(SocialType.GOOGLE, getGoogleService());
        map.put(SocialType.NAVER, getNaverService());
        return map;
    }

    @Bean
    public GoogleService getGoogleService() {
        return new GoogleService(clientId, mapper, service, jwtUtils, restTemplate);
    }

    @Bean
    public KakaoService getKakaoService() {
        return new KakaoService(kakaoAppKey, kakaoAdminKey, mapper, service, jwtUtils, restTemplate);
    }
    @Bean
    public NaverService getNaverService() {
        return new NaverService(naverClientId, naverClientSecret, mapper, service, jwtUtils, restTemplate);
    }

    @Bean
    public SocialServiceFactory socialServiceFactory() {
        return new SocialServiceFactory(socialTypeSocialServiceMap());
    }

}
