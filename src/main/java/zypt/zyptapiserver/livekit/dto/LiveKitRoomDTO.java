package zypt.zyptapiserver.livekit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LiveKitRoomDTO {
    String roomName;
    String roomId;
    int emptyTimeOut;
    int maxParticipants;
    int numParticipants;

    @Builder
    public LiveKitRoomDTO(String roomName, String roomId, int emptyTimeOut, int maxParticipants, int numParticipants) {
        this.roomName = roomName;
        this.roomId = roomId;
        this.emptyTimeOut = emptyTimeOut;
        this.maxParticipants = maxParticipants;
        this.numParticipants = numParticipants;
    }
}
