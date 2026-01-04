package zypt.zyptapiserver.livekit;

import lombok.extern.slf4j.Slf4j;
import zypt.zyptapiserver.dto.livekit.LiveKitAccessTokenDTO;
import io.livekit.server.*;
import livekit.LivekitModels;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import zypt.zyptapiserver.dto.livekit.LiveKitParticipantDTO;
import zypt.zyptapiserver.dto.livekit.LiveKitRoomDTO;
import zypt.zyptapiserver.exception.livekit.DeleteFailException;
import zypt.zyptapiserver.exception.livekit.RetrofitExecuteException;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class LiveKitService {

    private final LiveKitSource liveKitSource;
    private final RoomServiceClient client;

    public LiveKitService(LiveKitSource liveKitSource) {
        this.liveKitSource = liveKitSource;
        this.client = RoomServiceClient.createClient(
                liveKitSource.getURL(), liveKitSource.getAPI_KEY(), liveKitSource.getSECRET_KEY());
    }

    /** 프로세스
     * createRoom으로 방생성 (여기서 반환된 값으로 방 설정 제어 가능)
     * getLiveKitAccessToken으로 유저 방 연결 토큰 전달
     * userid는 AccessToken에서 꺼내서 사용
     */
    public LiveKitAccessTokenDTO getLiveKitAccessToken(String nickName, String memberId,  String roomName) {
        log.info("룸 참여 액세스 토큰 획득 시도 roomName={}", roomName);
        AccessToken livekitAccessToken = new AccessToken(liveKitSource.getAPI_KEY(), liveKitSource.getSECRET_KEY());

        // 유저 이름
        livekitAccessToken.setName(nickName);
        livekitAccessToken.setIdentity(memberId);
        livekitAccessToken.setMetadata("사용자 추가 정보");
        livekitAccessToken.setTtl(1000L); // 토큰 유효시간 초단위 (상대적) exp랑 같이 사용 불가

//        livekitAccessToken.setExpiration(new Date(Duration.ofHours(10).getSeconds())); // 토큰 종료 시간을 정함 (고정)
//        livekitAccessToken.setNotBefore(); // 특정 시간 이후에 토큰 효능 발휘
//        livekitAccessToken.setRoomPreset(); // 방 설정 서버에서 정의된 프리셋 사용

        // RoomJoin은 효능이 있음, 다만 RoomCreate는 설정해도 방이 자동 생성되므로 따로 에러 처리 필요
        livekitAccessToken.addGrants(new RoomJoin(true), new RoomName(roomName), new RoomCreate(false));
        log.info("룸 참여 액세스 토큰 획득 성공");

        return new LiveKitAccessTokenDTO(livekitAccessToken.toJwt(), new Date());
    }


    /**
     * 방생성 옵션 설정 및 livekit 액세스토큰 발급
     * @param nickName 닉네임
     * @param memberId 식별 유저 id
     * @param roomName 방 이름
     * @param maxParticipant  최대 참가자 수
     * @return
     */
    public LiveKitAccessTokenDTO createRoom(String nickName, String memberId, String roomName, int maxParticipant) {
        log.info("룸 생성 시도, roomName={}, maxParticipant={}", roomName, maxParticipant);
        // 방이름, 빈 방 타임아웃, 최대 참여자 수 제한
        Response<LivekitModels.Room> response
                = LiveKitTemplate.execute(() -> client.createRoom(roomName, 1000, maxParticipant).execute());
        LivekitModels.Room room = response.body();

        log.info("룸 생성 성공 createRoomInfo = {}", room);
        return getLiveKitAccessToken(nickName, memberId, roomName);

    }

    /**
     * 특정 방을 삭제
     * @param roomName
     * @return true
     */
    public void deleteRoom(String roomName) {
        log.info("룸 삭제 roomName={}", roomName);
        boolean isDeletedRoom = LiveKitTemplate.execute(() -> client.deleteRoom(roomName).execute().isSuccessful());


        // 방 삭제 실패시 에러를 던짐
        if (!isDeletedRoom) {
            throw new DeleteFailException("방 삭제 실패 : " + roomName);
        }

        log.info("룸 삭제 성공");
    }

    /**
     * 모든 방을 조회
     * @return 방 리스트
     */
    public List<LiveKitRoomDTO> findAllRooms() {
        log.info("모든 룸 리스트 조회");
        List<LivekitModels.Room> roomList
                = LiveKitTemplate.execute(() -> client.listRooms().execute().body());

        if (roomList == null || roomList.isEmpty()) {
            log.info("현재 룸이 없음");
            return null; // 빈 JSON 배열 반환
        }

        log.info("룸 조회 성공");
        return roomList.stream()
                .map(room -> new LiveKitRoomDTO(
                        room.getName(),
                        room.getSid(),
                        room.getEmptyTimeout(),
                        room.getMaxParticipants(),
                        room.getNumParticipants()
                ))
                .toList();
    }


    /**
     * 특정 방의 참가자들의 정보를 조회
     * @param roomName
     * @return 참가자 정보 DTO 리스트
     */
    public List<LiveKitParticipantDTO> getRoomParticipantsByRoomName(String roomName) {
        log.info("룸 참가자 정보 조회");
        // 해당 룸의 참가자 정보를 가져오기
        List<LivekitModels.ParticipantInfo> participants
                = LiveKitTemplate.execute(() -> client.listParticipants(roomName).execute().body());

        // 방의 참자가 정보를 가져오지 못하거나 비었다면 에러 던짐
        if (participants == null || participants.isEmpty()) {
            throw new RetrofitExecuteException("존재 하지 않는 방입니다. ");
        }

        log.info("룸 참가자 정보 조회 성공");
        // participantDTO 객체로 변환하여 list로 반환
        return participants.stream()
                .map(participantInfo -> new LiveKitParticipantDTO(
                                participantInfo.getIdentity(),
                                participantInfo.getName(),
                                participantInfo.getJoinedAt()
                        )
                )
                .toList();
    }
}

