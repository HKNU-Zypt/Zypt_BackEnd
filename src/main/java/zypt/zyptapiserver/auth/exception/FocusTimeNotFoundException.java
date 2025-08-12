package zypt.zyptapiserver.auth.exception;

public class FocusTimeNotFoundException extends RuntimeException {
    public FocusTimeNotFoundException(String message) {
        super(message);
    }

    public FocusTimeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
