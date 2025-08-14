package zypt.zyptapiserver.exception;

public class FocusTimeSaveFailedException extends RuntimeException {

    public FocusTimeSaveFailedException(String message) {
        super(message);
    }

    public FocusTimeSaveFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
