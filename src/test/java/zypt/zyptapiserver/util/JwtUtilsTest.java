package zypt.zyptapiserver.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void jwtRunCheck() {
        log.info("key = {} , act = {}, rft = {}", jwtUtils.getKey(), jwtUtils.getACCESS_TOKEN_EXPIRATION(), jwtUtils.getREFRESH_TOKEN_EXPIRATION());
    }

}