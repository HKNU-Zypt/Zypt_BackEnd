package zypt.zyptapiserver.auth.exception;

public class InvalidOidcPublicKeyException extends RuntimeException {

    public InvalidOidcPublicKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
