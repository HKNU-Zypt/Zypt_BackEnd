package zypt.zyptapiserver.config;

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

    private final KakaoService kakaoService;

    // 소셜 타입 , 소셜 서비스를 맵에 등록
    @Bean
    public Map<SocialType, SocialService> socialTypeSocialServiceMap(KakaoService kakaoService) {
        Map<SocialType, SocialService> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, kakaoService);
        map.put(SocialType.GOOGLE, null);
        map.put(SocialType.NAVER, null);
        return map;
    }

    @Bean
    public SocialServiceFactory socialServiceFactory() {
        return new SocialServiceFactory(socialTypeSocialServiceMap(kakaoService));
    }

}
