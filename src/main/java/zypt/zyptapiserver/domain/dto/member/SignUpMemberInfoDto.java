package zypt.zyptapiserver.domain.dto.member;

import jakarta.validation.constraints.NotNull;

public record SignUpMemberInfoDto(String id, @NotNull  String nickName) {
}
