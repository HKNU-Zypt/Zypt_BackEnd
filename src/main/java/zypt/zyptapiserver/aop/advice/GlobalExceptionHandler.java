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
        ApiErrorResponse response = ErrorCode.MEMBER_NOT_FOUND_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiErrorResponse> redisConnectionFailure(Exception ex) {
        ApiErrorResponse response = ErrorCode.REDIS_CONNECTION_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler({MissingTokenException.class, InvalidTokenException.class})
    public ResponseEntity<ApiErrorResponse> reLogin(Exception ex) {
        ApiErrorResponse response = ErrorCode.TOKEN_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiErrorResponse> restTemplateException(Exception ex) {
        ApiErrorResponse response = ErrorCode.EXTERNAL_API_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(OidcPublicKeyFetchException.class)
    public ResponseEntity<ApiErrorResponse> oidcPublicKeyFetchException(Exception ex) {
        ApiErrorResponse response = ErrorCode.SOCIAL_SERVER_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(InvalidOidcPublicKeyException.class)
    public ResponseEntity<ApiErrorResponse> invalidOidcPublicKeyException(Exception ex) {
        ApiErrorResponse response = ErrorCode.TOKEN_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorResponse> serverException(Exception ex) {
        ApiErrorResponse response = ErrorCode.INTERNAL_SERVER_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler({DataAccessException.class, FocusTimeSaveFailedException.class})
    public ResponseEntity<ApiErrorResponse> dataAccessException(Exception ex) {
        ApiErrorResponse response = ErrorCode.INTERNAL_SERVER_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEntry(Exception ex) {
        ApiErrorResponse response = ErrorCode.DATA_INTEGRITY_VIOLATION_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> noSuchElementException(Exception ex) {
        ApiErrorResponse response = ErrorCode.NOT_FOUND_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(FocusTimeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> focusTimeNotFoundException(Exception ex) {
        ApiErrorResponse response = ErrorCode.FOCUS_TIME_NOT_FOUND_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ApiErrorResponse> invalidParamException(Exception ex) {
        ApiErrorResponse response = ErrorCode.BAD_REQUEST_PARAMETER_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponse response = ErrorCode.NOT_FOUND_ERROR.getApiErrorResponse(ex.getMessage());
        logError(response);
        return ResponseEntity
                .status(status)
                .body(response);
    }

    private void logError(ApiErrorResponse response) {
        log.info("error code = {} \n message = {} \n detail = {}",
                response.getCode(), response.getMessage(), response.getDetail());
    }


}
