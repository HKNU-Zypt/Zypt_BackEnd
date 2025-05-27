package zypt.zyptapiserver.domain.dto;

import zypt.zyptapiserver.domain.enums.SocialType;

public record SocialLoginDto(SocialType type, String token) {
}
