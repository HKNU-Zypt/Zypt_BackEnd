package zypt.Zyptapiserver.livekit.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;


@Getter
@ToString
@AllArgsConstructor
public class LiveKitAccessTokenDTO {
    String livekitAccessToken;
    Date createAt;
}
