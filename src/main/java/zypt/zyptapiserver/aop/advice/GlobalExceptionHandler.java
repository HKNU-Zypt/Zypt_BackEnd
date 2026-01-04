package zypt.zyptapiserver.aop.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import zypt.zyptapiserver.dto.ApiErrorResponse;
import zypt.zyptapiserver.domain.enums.ErrorCode;
import zypt.zyptapiserver.exception.*;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> memberNotFoundException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorCode.MEMBER_NOT_FOUND_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiErrorResponse> redisConnectionFailure(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.REDIS_CONNECTION_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({MissingTokenException.class, InvalidTokenException.class})
    public ResponseEntity<ApiErrorResponse> reLogin(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorCode.TOKEN_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiErrorResponse> restTemplateException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.EXTERNAL_API_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(OidcPublicKeyFetchException.class)
    public ResponseEntity<ApiErrorResponse> oidcPublicKeyFetchException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.SOCIAL_SERVER_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidOidcPublicKeyException.class)
    public ResponseEntity<ApiErrorResponse> invalidOidcPublicKeyException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorCode.TOKEN_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorResponse> serverException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.INTERNAL_SERVER_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({DataAccessException.class, FocusTimeSaveFailedException.class})
    public ResponseEntity<ApiErrorResponse> dataAccessException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.INTERNAL_SERVER_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEntry(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorCode.DATA_INTEGRITY_VIOLATION_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> noSuchElementException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorCode.NOT_FOUND_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(FocusTimeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> focusTimeNotFoundException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorCode.FOCUS_TIME_NOT_FOUND_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ApiErrorResponse> invalidParamException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorCode.BAD_REQUEST_PARAMETER_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }

    // 기존 스프링 404 예외
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorCode.NOT_FOUND_ERROR
                        .getApiErrorResponse(ex.getMessage()));
    }



}
