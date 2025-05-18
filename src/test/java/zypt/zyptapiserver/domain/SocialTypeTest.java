package zypt.zyptapiserver.domain;

import org.junit.jupiter.api.DisplayName;
import zypt.zyptapiserver.auth.service.KakaoService;
import zypt.zyptapiserver.auth.service.SocialService;
import zypt.zyptapiserver.auth.service.SocialServiceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import zypt.zyptapiserver.domain.enums.SocialType;

import java.util.EnumMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
class SocialTypeTest {

    SocialServiceFactory socialServiceFactory = new SocialServiceFactory(socialTypeSocialServiceMap(new KakaoService()));
    MockMvc mockMvc;

    public Map<SocialType, SocialService> socialTypeSocialServiceMap(KakaoService kakaoService) {
        Map<SocialType, SocialService> map = new EnumMap<>(SocialType.class);
        map.put(SocialType.KAKAO, kakaoService);
        map.put(SocialType.GOOGLE, kakaoService);

        return map;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(socialServiceFactory).build();
    }
    @Test
    @DisplayName("소셜 로그인 헤더로 요청시 소셜로그인 provider에 해당하는 소셜 서비스 호출 확인 테스트")
    void socialTypeCheck() throws Exception {
        ResultActions perform = mockMvc.perform(get("/a")
                .header("SocialType", "google"));

        SocialService service = socialServiceFactory.getService(perform.andReturn().getRequest());

        Assertions.assertEquals(new KakaoService(), service);
    }

}