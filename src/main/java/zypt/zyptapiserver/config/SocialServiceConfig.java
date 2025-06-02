package zypt.zyptapiserver.config;

import org.springframework.beans.factory.annotation.Value;
import zypt.zyptapiserver.auth.service.*;
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

    private final JwtUtils jwtUtils;

    // 소셜 타입 , 소셜 서비스를 맵에 등록
    public Map<SocialType, SocialService> socialTypeSocialServiceMap() {
        Map<SocialType, SocialService> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, new KakaoServiceV1(kakaoAppKey, jwtUtils));
        map.put(SocialType.GOOGLE, new GoogleService(clientId));
        map.put(SocialType.NAVER, null);
        return map;
    }

    @Bean
    public SocialServiceFactory socialServiceFactory() {
        return new SocialServiceFactory(socialTypeSocialServiceMap());
    }

}
