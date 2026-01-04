package zypt.zyptapiserver.dto.member;

import jakarta.validation.constraints.NotNull;

public record SignUpMemberInfoDto(String id, @NotNull  String nickName) {
}
