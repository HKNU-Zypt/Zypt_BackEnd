package zypt.zyptapiserver.livekit.dto;

public record LiveKitRoomDTO(String roomName, String roomId, int emptyTimeOut, int maxParticipants, int numParticipants) {

}
