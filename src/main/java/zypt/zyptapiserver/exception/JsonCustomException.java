package zypt.zyptapiserver.exception;

public class JsonCustomException extends RuntimeException {

    public JsonCustomException(String message) {
        super(message);
    }

    public JsonCustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonCustomException(Throwable cause) {
        super(cause);
    }
}
