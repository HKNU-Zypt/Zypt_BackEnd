package zypt.zyptapiserver.exception.livekit;

import java.io.Serial;

public class DeleteFailException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 756232949026631512L;

    public DeleteFailException(String message) {
        super(message);
    }
}
