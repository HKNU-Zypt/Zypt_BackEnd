package zypt.zyptapiserver.exception;

public class OidcAuthenticationException extends RuntimeException{
    public OidcAuthenticationException() {
    }

    public OidcAuthenticationException(String message) {
        super(message);
    }

    public OidcAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OidcAuthenticationException(Throwable cause) {
        super(cause);
    }

    public OidcAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
