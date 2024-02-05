package dragonfly.ews.domain.project.exception;

public class CannotChangeProjectOwnerException extends RuntimeException {
    public CannotChangeProjectOwnerException() {
    }

    public CannotChangeProjectOwnerException(String message) {
        super(message);
    }

    public CannotChangeProjectOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotChangeProjectOwnerException(Throwable cause) {
        super(cause);
    }

    public CannotChangeProjectOwnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
