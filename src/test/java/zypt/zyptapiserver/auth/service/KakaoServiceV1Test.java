package zypt.zyptapiserver.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import zypt.zyptapiserver.auth.user.UserInfo;
import zypt.zyptapiserver.config.SocialServiceConfig;
import zypt.zyptapiserver.domain.enums.SocialType;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class KakaoServiceV1Test {

    @Autowired
    private SocialServiceFactory factory;


    @Test
    @DisplayName("id토큰값으로 검증 및 UserInfo 객체 생성 테스트")
    void oidcTest() {
        String idToken = "eyJraWQiOiI5ZjI1MmRhZGQ1ZjIzM2Y5M2QyZmE1MjhkMTJmZWEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI1ZjE4ZjQ0MmE4OTFmYWNlZDRhM2E4NWEzYWI2NDhkYyIsInN1YiI6IjQwMDI3OTkxMTAiLCJhdXRoX3RpbWUiOjE3NDg4NzA4MzcsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwibmlja25hbWUiOiLtl4jssKzsmrEiLCJleHAiOjE3NDg5MTQwMzcsImlhdCI6MTc0ODg3MDgzNywiZW1haWwiOiJzaWFpMzY0N0BnbWFpbC5jb20ifQ.GG_-VFtpWpnt9aC9zCjKswDE0uXSd-UwJy73cInGT6v99ap_YCHs35ahH8iv26bYashz6oKV2t8R7dKB1pGj8rmBVUiCQdecl8BG8y1Ygw6dSaHU6vjbmef8CXNqfdM1KfKAmfdwj8OVG9WAXokAt9YGSpIZtKvzvzqOnYZ5_ZyantaRE9vamvS7Bew1freTTxR-TPshyF9b0MrdqPLHmXVi4xCNPNLHLkYZvMVbUXWVQozJrYUMUhDXQYhVvtIuuLNesS93wyGeKDhBuSHNz3kLI3PRnOoOFjXPC7tfi48OvYuo6S74JaC270331UAhwsHyn-4-WW3PrJm-LrcfjQ";
        SocialService service = factory.getService(SocialType.KAKAO);
        UserInfo userInfo = service.getUserInfo(idToken);
        log.info("info ={}", userInfo);

        Assertions.assertThat(userInfo).isNotNull();

    }

}