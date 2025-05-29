package zypt.zyptapiserver.config;

import org.springframework.beans.factory.annotation.Value;
import zypt.zyptapiserver.auth.service.GoogleService;
import zypt.zyptapiserver.auth.service.KakaoService;
import zypt.zyptapiserver.auth.service.SocialService;
import zypt.zyptapiserver.auth.service.SocialServiceFactory;
import zypt.zyptapiserver.domain.enums.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SocialServiceConfig {

    @Value("${google.CLIENT_ID}")
    private String clientId;

    // 소셜 타입 , 소셜 서비스를 맵에 등록
    public Map<SocialType, SocialService> socialTypeSocialServiceMap() {
        Map<SocialType, SocialService> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, new KakaoService());
        map.put(SocialType.GOOGLE, new GoogleService(clientId));
        map.put(SocialType.NAVER, null);
        return map;
    }

    @Bean
    public SocialServiceFactory socialServiceFactory() {
        return new SocialServiceFactory(socialTypeSocialServiceMap());
    }

}
