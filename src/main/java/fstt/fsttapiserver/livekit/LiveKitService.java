package fstt.fsttapiserver.livekit;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.livekit.server.*;
import livekit.LivekitModels;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;
import java.util.List;

@Service

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

    public String getLiveKitAccessToken(String name, String userid,  String roomName) {
        AccessToken livekitAccessToken = new AccessToken(liveKitSource.getAPI_KEY(), liveKitSource.getSECRET_KEY());

        // 유저 이름
        livekitAccessToken.setName(name);
        livekitAccessToken.setIdentity(userid);
        livekitAccessToken.setMetadata("사용자 추가 정보");
        livekitAccessToken.setTtl(1000L); // 토큰 유효시간 초단위 (상대적) exp랑 같이 사용 불가
//        livekitAccessToken.setExpiration(new Date(Duration.ofHours(10).getSeconds())); // 토큰 종료 시간을 정함 (고정)
//        livekitAccessToken.setNotBefore(); // 특정 시간 이후에 토큰 효능 발휘
//        livekitAccessToken.setRoomPreset(); // 방 설정 서버에서 정의된 프리셋 사용

        // RoomJoin은 효능이 있음, 다만 RoomCreate는 설정해도 방이 자동 생성되므로 따로 에러 처리 필요
        livekitAccessToken.addGrants(new RoomJoin(true), new RoomName(roomName), new RoomCreate(false));

        return livekitAccessToken.toJwt();
    }

    // 방 설정 정도 생성
    public String createRoom(String name, String userid, String roomName) throws IOException {

        // 방이름, 빈 방 타임아웃, 최대 참여자 수 제한 (이정도만 사용하면 된다.)
        Call<LivekitModels.Room> call = client.createRoom(roomName, 1000, 10);
//        Call<LivekitModels.Room> call = client.createRoom(roomName, 1000, 10, "nodeId", "metadata", 0, 10, true, 1000);
        Response<LivekitModels.Room> response = call.execute();
        LivekitModels.Room room = response.body();
        return getLiveKitAccessToken(name, userid, roomName);

    }

    public boolean deleteRoom(String roomName) {
        client.deleteRoom(roomName);
        return true;
    }

    public String listRoom() throws IOException {
        List<LivekitModels.Room> body = client.listRooms().execute().body();
        if (body == null || body.isEmpty()) {
            return "[]"; // 빈 JSON 배열 반환
        }

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (LivekitModels.Room room : body) {
            String roomJson = JsonFormat.printer().print((MessageOrBuilder) room);
            jsonBuilder.append(roomJson).append(",");
        }
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 1); // 마지막 쉼표 제거
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }



}
