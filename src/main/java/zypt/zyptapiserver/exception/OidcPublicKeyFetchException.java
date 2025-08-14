package zypt.zyptapiserver.exception;

public class OidcPublicKeyFetchException extends RuntimeException {

    public OidcPublicKeyFetchException() {
        super();
    }

    public OidcPublicKeyFetchException(String message) {
        super(message);
    }

    public OidcPublicKeyFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
