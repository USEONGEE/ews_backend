package dragonfly.ews.domain.member.exception;

public class CannotChangePasswordException extends RuntimeException {
    public CannotChangePasswordException() {
    }

    public CannotChangePasswordException(String message) {
        super(message);
    }

    public CannotChangePasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotChangePasswordException(Throwable cause) {
        super(cause);
    }

    public CannotChangePasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
