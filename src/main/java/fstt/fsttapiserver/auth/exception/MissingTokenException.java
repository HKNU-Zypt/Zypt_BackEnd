package fstt.fsttapiserver.auth.exception;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String message) {
        super(message);
    }
}
