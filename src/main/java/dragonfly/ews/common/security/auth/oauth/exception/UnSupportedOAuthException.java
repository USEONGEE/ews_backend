package dragonfly.ews.common.security.auth.oauth.exception;

public class UnSupportedOAuthException extends RuntimeException {
    public UnSupportedOAuthException() {

    }

    public UnSupportedOAuthException(String message) {
        super(message);
    }

    public UnSupportedOAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportedOAuthException(Throwable cause) {
        super(cause);
    }

    public UnSupportedOAuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
