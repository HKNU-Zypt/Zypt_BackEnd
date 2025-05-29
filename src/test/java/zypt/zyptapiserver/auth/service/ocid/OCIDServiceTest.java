package zypt.zyptapiserver.auth.service.ocid;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



@Slf4j
@SpringBootTest
class OCIDServiceTest {

    OCIDService service = new OCIDService();

    @Test
    void getOpenKey() {

        long l = System.currentTimeMillis();
        service.getOpenIdPublicKeys();
        long l2 = System.currentTimeMillis();

        long l3 = System.currentTimeMillis();
        service.getOpenIdPublicKeys();
        long l4 = System.currentTimeMillis();

        log.info("result = {} vs {}" , l2- l, l4 -l3);
    }

}