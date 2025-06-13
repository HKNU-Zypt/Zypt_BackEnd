package zypt.zyptapiserver.auth.exception;

import java.io.Serial;

public class MissingTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5005103919901563217L;

    public MissingTokenException(String message) {
        super(message);
    }
}
