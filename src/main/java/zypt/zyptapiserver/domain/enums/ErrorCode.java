package zypt.zyptapiserver.domain.enums;

import lombok.extern.slf4j.Slf4j;
import zypt.zyptapiserver.dto.ApiErrorResponse;

@Slf4j
public enum ErrorCode {

    // 일반적인 에러
    INTERNAL_SERVER_ERROR(-1, "서버 내부 처리 실패 또는 오류 발생, 재시도"),
    NOT_FOUND_ERROR(-2, "존재하지 않는 URL 요청, URL 확인"),
    TOKEN_ERROR(-3, "만료 혹은 비정상 토큰, 재 로그인 시도"),
    BAD_REQUEST_PARAMETER_ERROR(-4, "잘못된 파라미터, 요청 파라미터 재확인"),
    UNAUTHORIZED_ERROR(-5, "인증되지 않은 사용자, 로그인 필요"),
    FORBIDDEN_ERROR(-6, "요청 권한이 없습니다"),
    METHOD_NOT_ALLOWED_ERROR(-7, "허용되지 않은 HTTP Method 요청"),
    UNSUPPORTED_MEDIA_TYPE_ERROR(-8, "지원하지 않는 Content-Type 요청"),


    // 외부 통신 에러
    EXTERNAL_API_ERROR(-10, "외부 API 호출 중 오류 발생, 외부 API 점검 필요"),
    SOCIAL_SERVER_ERROR(-11, "소셜 서버 통신 오류, 잘못된 토큰 또는 서버 장애"),
    LIVE_KIT_API_ERROR(-12, "Livekit 클라우드 API 통신 오류 발생, LiveKit 라이브러리 및 API 점검 필요"),

    // 도메인 에러
    MEMBER_NOT_FOUND_ERROR(-21, "해당 회원이 존재하지 않음, 회원 여부 확인"),
    SOCIAL_REFRESH_TOKEN_NOT_FOUND_ERROR(-22, "해당 회원의 RefreshToken 존재하지 않음, 재로그인으로 토큰 획득 시도"),
    FOCUS_TIME_NOT_FOUND_ERROR(-23, "해당 FocusTime 데이터가 존재하지 않음, 요청 날짜를 다시 확인"),

    DATA_INTEGRITY_VIOLATION_ERROR(-33, "무결성 위배, 유니크, 중복, not null, 외래키 등 확인"),

    // 심각한 서버 에러
    REDIS_CONNECTION_ERROR(-101, "Redis 연결 실패. 서버 상태 점검 필요");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiErrorResponse getApiErrorResponse() {
        return new ApiErrorResponse(this.code, this.message);
    }

    public ApiErrorResponse getApiErrorResponse(String detail) {
        log.info("error code = {} \n message = {} \n detail = {}" , this.code, this.message, detail);
        return new ApiErrorResponse(this.code, this.message, detail);
    }

}
