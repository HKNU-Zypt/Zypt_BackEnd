package zypt.zyptapiserver.exception;

public class InvalidOidcPublicKeyException extends RuntimeException {

    public InvalidOidcPublicKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOidcPublicKeyException(String message) {
        super(message);
    }
}
