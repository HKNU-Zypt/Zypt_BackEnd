package zypt.Zyptapiserver.livekit;

import zypt.Zyptapiserver.livekit.dto.LiveKitAccessTokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@Slf4j
@SpringBootTest
class LiveKitServiceTest {

    @Autowired
    LiveKitService liveKitService;

    @Test
    void run() throws IOException {
        LiveKitAccessTokenDTO myRoom = liveKitService.createRoom("1v", "1234", "myRoom");
        LiveKitAccessTokenDTO myRoom1 = liveKitService.getLiveKitAccessToken("2v", "4321", "myRoom");

        log.info("room = {}", myRoom);
        log.info("room2 = {}", myRoom1.getLivekitAccessToken());
        log.info("list = {}", liveKitService.listRoom());
    }

    @Test
    void notCreateRoomAndJoin() throws IOException {
//        String myRoom = liveKitService.createRoom("1v", "1234", "myRoom");


        LiveKitAccessTokenDTO myRoom2 = liveKitService.getLiveKitAccessToken("2v", "4321", "myRoom");
        LiveKitAccessTokenDTO myRoom3 = liveKitService.getLiveKitAccessToken("3v", "5321", "myRoom");
//        log.info("room = {}", myRoom);
        log.info("room2 = {}", myRoom2);
        log.info("room3 = {}", myRoom3);

    }

    @Test
    void deleteRoomNow() throws IOException {
        if (liveKitService.deleteRoom("myRoom")) {
            log.info("룸 삭제 성공");
        }

        log.info("list = {}", liveKitService.listRoom());

    }

}