package zypt.zyptapiserver.domain.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequestDto(@NotNull String refreshToken) {
}
