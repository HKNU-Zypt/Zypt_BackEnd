package zypt.zyptapiserver.exception;

public class ExpiredIdTokenException extends RuntimeException {
    public ExpiredIdTokenException() {
    }

    public ExpiredIdTokenException(String message) {
        super(message);
    }

    public ExpiredIdTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredIdTokenException(Throwable cause) {
        super(cause);
    }

    public ExpiredIdTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
