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

    private final GoogleOIDCService googleOIDCService;
    private final NaverService naverService;
    private final KakaoService kakaoService;

    // 소셜 타입 , 소셜 서비스를 맵에 등록
    public Map<SocialType, SocialService> socialTypeSocialServiceMap() {
        Map<SocialType, SocialService> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, kakaoService);
        map.put(SocialType.GOOGLE, googleOIDCService);
        map.put(SocialType.NAVER, naverService);
        return map;
    }

    @Bean
    public SocialServiceFactory socialServiceFactory() {
        return new SocialServiceFactory(socialTypeSocialServiceMap());
    }

}
