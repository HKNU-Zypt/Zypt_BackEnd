package zypt.zyptapiserver.exception;

import java.io.Serial;

public class MemberNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -8839247615577430465L;

    public MemberNotFoundException(String message) {
        super(message);
    }
}
