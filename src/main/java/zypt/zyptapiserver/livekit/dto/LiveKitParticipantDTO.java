package zypt.zyptapiserver.livekit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class LiveKitParticipantDTO {
    String id;
    String nickName;
    Long joinedAt; // 참여 시간 (초 단위 유닉스 타임스탬프)


    @Builder
    public LiveKitParticipantDTO(String id, String userName, Long joinedAt) {
        this.id = id;
        this.nickName = userName;
        this.joinedAt = joinedAt;
    }

}
