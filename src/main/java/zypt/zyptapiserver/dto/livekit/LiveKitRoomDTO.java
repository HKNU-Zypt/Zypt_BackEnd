package zypt.zyptapiserver.dto.livekit;

public record LiveKitRoomDTO(String roomName, String roomId, int emptyTimeOut, int maxParticipants, int numParticipants) {

}
