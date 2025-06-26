package zypt.zyptapiserver.aop.advice;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import zypt.zyptapiserver.auth.exception.InvalidTokenException;
import zypt.zyptapiserver.auth.exception.MissingTokenException;
import zypt.zyptapiserver.exception.MemberNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> memberNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({RedisConnectionFailureException.class,
                        DataAccessException.class})
    public ResponseEntity<String> redisConnectionFailure(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler({MissingTokenException.class, InvalidTokenException.class})
    public ResponseEntity<String> reLogin(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 만료되었거나 비정상 토큰입니다. 다시 로그인해주세요 " + ex.getMessage());
    }
}
