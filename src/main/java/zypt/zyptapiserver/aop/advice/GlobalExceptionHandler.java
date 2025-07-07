package zypt.zyptapiserver.aop.advice;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import zypt.zyptapiserver.auth.exception.InvalidTokenException;
import zypt.zyptapiserver.auth.exception.MissingTokenException;
import zypt.zyptapiserver.auth.exception.InvalidOidcPublicKeyException;
import zypt.zyptapiserver.controller.AuthController;
import zypt.zyptapiserver.controller.FocusTimeController;
import zypt.zyptapiserver.controller.LiveKitController;
import zypt.zyptapiserver.controller.MemberController;
import zypt.zyptapiserver.exception.MemberNotFoundException;
import zypt.zyptapiserver.auth.exception.OidcPublicKeyFetchException;

import java.util.NoSuchElementException;

@RestControllerAdvice(annotations = RestController.class, basePackageClasses = {AuthController.class, MemberController.class, LiveKitController.class, FocusTimeController.class})
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> memberNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<String> redisConnectionFailure(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler({MissingTokenException.class, InvalidTokenException.class})
    public ResponseEntity<String> reLogin(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 만료되었거나 비정상 토큰입니다. 다시 로그인해주세요 " + ex.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> restTemplateException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("다른 서버에 대한 API 요청을 실패하였습니다. 관리자에게 문의" + ex.getMessage());
    }

    @ExceptionHandler(OidcPublicKeyFetchException.class)
    public ResponseEntity<String> oidcPublicKeyFetchException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OIDCPublicKey 획득 실패 관리자에게 문의 " + ex.getMessage());
    }

    @ExceptionHandler(InvalidOidcPublicKeyException.class)
    public ResponseEntity<String> invalidOidcPublicKeyException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("RSA 공개키 생성 실패, 재로그인 혹은 OIDC 목록 재조회 " + ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> serverException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에 문제가 있습니다. " + ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> dataAccessException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에 문제가 있습니다 관리자를 호출하세요 " + ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청한 내용은 DB에 존재하지 않습니다. " + ex.getMessage());
    }
}
