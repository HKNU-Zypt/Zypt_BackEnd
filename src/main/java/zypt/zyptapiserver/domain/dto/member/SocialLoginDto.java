package zypt.zyptapiserver.domain.dto.member;

import zypt.zyptapiserver.domain.enums.SocialType;

public record SocialLoginDto(SocialType type, String token) {
}
