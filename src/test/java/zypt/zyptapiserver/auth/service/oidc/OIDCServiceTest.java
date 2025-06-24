package zypt.zyptapiserver.auth.service.oidc;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import zypt.zyptapiserver.domain.enums.SocialType;


@Slf4j
@SpringBootTest
class OIDCServiceTest {

    @Autowired
    CacheManager cacheManager;
    OIDCService service = new OIDCService();

    @Test
    void openKeyCacheTest() throws JsonProcessingException {

//        long l = System.currentTimeMillis();
//        service.getOpenIdPublicKeys(SocialType.KAKAO);
//        long l2 = System.currentTimeMillis();
//
//        long l3 = System.currentTimeMillis();
//        service.getOpenIdPublicKeys(SocialType.KAKAO);
//        long l4 = System.currentTimeMillis();
//
//        log.info("result = {} vs {}" , l2- l, l4 -l3);
    }


}