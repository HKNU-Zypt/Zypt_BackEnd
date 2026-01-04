package zypt.zyptapiserver.dto.livekit;

// 참여 시간 (초 단위 유닉스 타임스탬프)
public record LiveKitParticipantDTO (String id ,String nickName, Long joinedAt) {

}
