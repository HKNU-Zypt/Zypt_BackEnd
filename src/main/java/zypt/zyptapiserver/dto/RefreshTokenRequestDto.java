package zypt.zyptapiserver.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequestDto(@NotNull String refreshToken) {
}
