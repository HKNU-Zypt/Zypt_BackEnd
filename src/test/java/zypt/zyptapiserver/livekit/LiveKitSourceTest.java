package zypt.zyptapiserver.livekit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class LiveKitSourceTest {

    @Autowired
    LiveKitSource source;

    @Test
    void run() {
        String url = source.getURL();
        log.info("uri = {}", url);
        log.info("key = {}", source.getAPI_KEY());
    }

}