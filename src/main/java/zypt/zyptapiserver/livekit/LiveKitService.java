package zypt.zyptapiserver.livekit;

import lombok.extern.slf4j.Slf4j;
import zypt.zyptapiserver.livekit.dto.LiveKitAccessTokenDTO;
import io.livekit.server.*;
import livekit.LivekitModels;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import zypt.zyptapiserver.livekit.dto.LiveKitParticipantDTO;
import zypt.zyptapiserver.livekit.dto.LiveKitRoomDTO;
import zypt.zyptapiserver.livekit.exception.DeleteFailException;
import zypt.zyptapiserver.livekit.exception.RetrofitExecuteException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LiveKitService {

    private final LiveKitSource liveKitSource;
    private final RoomServiceClient client;

    public LiveKitService(LiveKitSource liveKitSource) {
        this.liveKitSource = liveKitSource;
        this.client = RoomServiceClient.createClient(
                liveKitSource.getURL(), liveKitSource.getAPI_KEY(), liveKitSource.getSECRET_KEY());
    }

    // 프로세스
    // createRoom으로 방생성 (여기서 반환된 값으로 방 설정 제어 가능)
    // getLiveKitAccessToken으로 유저 방 연결 토큰 전달
    // userid는 AccessToken에서 꺼내서 사용
    public LiveKitAccessTokenDTO getLiveKitAccessToken(String nickName, String userId,  String roomName) {
        AccessToken livekitAccessToken = new AccessToken(liveKitSource.getAPI_KEY(), liveKitSource.getSECRET_KEY());

        // 유저 이름
        livekitAccessToken.setName(nickName);
        livekitAccessToken.setIdentity(userId);
        livekitAccessToken.setMetadata("사용자 추가 정보");
        livekitAccessToken.setTtl(1000L); // 토큰 유효시간 초단위 (상대적) exp랑 같이 사용 불가
//        livekitAccessToken.setExpiration(new Date(Duration.ofHours(10).getSeconds())); // 토큰 종료 시간을 정함 (고정)
//        livekitAccessToken.setNotBefore(); // 특정 시간 이후에 토큰 효능 발휘
//        livekitAccessToken.setRoomPreset(); // 방 설정 서버에서 정의된 프리셋 사용

        // RoomJoin은 효능이 있음, 다만 RoomCreate는 설정해도 방이 자동 생성되므로 따로 에러 처리 필요
        livekitAccessToken.addGrants(new RoomJoin(true), new RoomName(roomName), new RoomCreate(false));

        return new LiveKitAccessTokenDTO(livekitAccessToken.toJwt(), new Date());
    }

    // 방 설정 정도 생성
    // 나중에 글로벌로 exception 잡아서 처리
    public LiveKitAccessTokenDTO createRoom(String nickName, String userId, String roomName, int maxParticipant) throws IOException {
        Call<LivekitModels.Room> call =
                LiveKitTemplate.execute(() -> client.createRoom(roomName, 1000, maxParticipant));

        // 방이름, 빈 방 타임아웃, 최대 참여자 수 제한 (이정도만 사용하면 된다.)
//        Call<LivekitModels.Room> call = client.createRoom(roomName, 1000, 10);
        Response<LivekitModels.Room> response = call.execute();
        LivekitModels.Room room = response.body();

        log.info("createRoomInfo = {}", room);

        return getLiveKitAccessToken(nickName, userId, roomName);

    }

    public boolean deleteRoom(String roomName) {
        boolean result = LiveKitTemplate.execute(() -> client.deleteRoom(roomName).execute().isSuccessful());
        // 삭제후 방 조회시 비어있다면 삭제된 것이므로 true 반환
            if (result) {
                return true;
                // 방 삭제 실패시 에러를 던짐
            } else {
                throw new DeleteFailException("방 삭제 실패 : " + roomName);
            }
    }

    public List<LiveKitRoomDTO> findAllRooms() {
        List<LivekitModels.Room> roomList
                = LiveKitTemplate.execute(() -> client.listRooms().execute().body());

        if (roomList == null || roomList.isEmpty()) {
            return null; // 빈 JSON 배열 반환
        }

        return roomList.stream()
                .map(room -> LiveKitRoomDTO.builder()
                        .roomId(room.getSid())
                        .roomName(room.getName())
                        .emptyTimeOut(room.getEmptyTimeout())
                        .maxParticipants(room.getMaxParticipants())
                        .numParticipants(room.getNumParticipants())
                        .build())
                .toList();
    }


    public List<LiveKitParticipantDTO> getRoomParticipantsByRoomName(String roomName) {

        // 해당 룸의 참가자 정보를 가져오기
        List<LivekitModels.ParticipantInfo> participants
                = LiveKitTemplate.execute(() -> client.listParticipants(roomName).execute().body());

        // 방의 참자가 정보를 가져오지 못하거나 비었다면 에러 던짐
        if (participants == null || participants.isEmpty()) {
            throw new RetrofitExecuteException("존재 하지 않는 방입니다. ");
        }

        // participantDTO 객체로 변환하여 list로 반환
        return participants.stream()
                .map(participantInfo -> LiveKitParticipantDTO
                        .builder()
                        .id(participantInfo.getIdentity())
                        .userName(participantInfo.getName())
                        .joinedAt(participantInfo.getJoinedAt())
                        .build())
                .toList();
    }
}
