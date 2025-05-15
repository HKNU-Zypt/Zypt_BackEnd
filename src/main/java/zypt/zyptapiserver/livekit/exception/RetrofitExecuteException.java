package zypt.zyptapiserver.livekit.exception;

import java.io.Serial;

public class RetrofitExecuteException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2897105799628980508L;


    public RetrofitExecuteException(String message) {
        super(message);
    }

    public RetrofitExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
