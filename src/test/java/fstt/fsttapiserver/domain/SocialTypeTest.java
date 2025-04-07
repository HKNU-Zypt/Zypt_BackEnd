package fstt.fsttapiserver.domain;

import fstt.fsttapiserver.auth.service.KakaoService;
import fstt.fsttapiserver.auth.service.SocialService;
import fstt.fsttapiserver.auth.service.SocialServiceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    void socialTypeCheck() throws Exception {
        ResultActions perform = mockMvc.perform(get("/a")
                .header("SocialType", "google"));

        SocialService service = socialServiceFactory.getService(perform.andReturn().getRequest());

        Assertions.assertEquals(new KakaoService(), service);
    }

}