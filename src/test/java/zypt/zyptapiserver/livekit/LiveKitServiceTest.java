package zypt.zyptapiserver.livekit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import zypt.zyptapiserver.livekit.dto.LiveKitAccessTokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zypt.zyptapiserver.livekit.dto.LiveKitParticipantDTO;
import zypt.zyptapiserver.livekit.dto.LiveKitRoomDTO;

import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootTest
class LiveKitServiceTest {

    @Autowired
    LiveKitService liveKitService;

    @Test
    void run() throws IOException {
        LiveKitAccessTokenDTO myRoom = liveKitService.createRoom("1v", "1234", "myRoom", 4);
        LiveKitAccessTokenDTO myRoom1 = liveKitService.getLiveKitAccessToken("2v", "4321", "myRoom");

        log.info("room = {}", myRoom);
        log.info("room2 = {}", myRoom1.getLivekitAccessToken());
        log.info("list = {}", liveKitService.findAllRooms());
    }

    @Test
    @DisplayName("중복 생성 테스트")
    void dupleCreateRoom() throws IOException {
        LiveKitAccessTokenDTO myRoom1 = liveKitService.createRoom("1v", "1234", "myRoom", 2);
        LiveKitAccessTokenDTO myRoom2 = liveKitService.createRoom("2v", "124", "myRoom", 4);

        log.info("room1 = {}", myRoom1);
        log.info("room2 = {}", myRoom2);



    }
    @Test
    @DisplayName("방 조인 요청이 방이 생성되는지 테스트")
    void notCreateRoomAndJoin() throws IOException {
//        String myRoom = liveKitService.createRoom("1v", "1234", "myRoom");


        LiveKitAccessTokenDTO myRoom2 = liveKitService.getLiveKitAccessToken("2v", "4321", "myRoom");
        LiveKitAccessTokenDTO myRoom3 = liveKitService.getLiveKitAccessToken("3v", "5321", "myRoom");
//        log.info("room = {}", myRoom);
        log.info("room2 = {}", myRoom2);
        log.info("room3 = {}", myRoom3);

        // 참여자 리스트
        List<LiveKitParticipantDTO> myRoom = liveKitService.getRoomParticipantsByRoomName("myRoom");
        log.info("roomParticipants = {}", myRoom);

    }

    @Test
    @DisplayName("모든 방 조회 후 특정 방의 참여자를 출력하고 방삭제 테스트")
    void getListParticipant() {
        List<LiveKitRoomDTO> allRooms = liveKitService.findAllRooms();
        log.info("rooms = {}", allRooms);

        List<LiveKitParticipantDTO> myRoom = liveKitService.getRoomParticipantsByRoomName("myRoom");
        log.info("roomParticipants = {}", myRoom);

        boolean myRoom1 = liveKitService.deleteRoom("myRoom");
        Assertions.assertThat(myRoom1).isTrue();

    }


    @Test
    @DisplayName("방삭제 테스트")
    void deleteRoomNow()  {
//        LiveKitAccessTokenDTO myRoom = liveKitService.createRoom("1v", "1234", "myRoom");

        if (liveKitService.deleteRoom("myRoom")) {
            log.info("룸 삭제 성공");
        }

        log.info("list = {}", liveKitService.findAllRooms());

    }

}