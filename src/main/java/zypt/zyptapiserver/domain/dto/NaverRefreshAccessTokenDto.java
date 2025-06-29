package zypt.zyptapiserver.domain.dto;

public record NaverRefreshAccessTokenDto(String access_token, String refresh_token, String token_type, String expires_in) {
}
