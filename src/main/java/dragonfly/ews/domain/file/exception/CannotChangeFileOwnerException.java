package dragonfly.ews.domain.file.exception;

public class CannotChangeFileOwnerException extends RuntimeException {
    public CannotChangeFileOwnerException() {
    }

    public CannotChangeFileOwnerException(String message) {
        super(message);
    }

    public CannotChangeFileOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotChangeFileOwnerException(Throwable cause) {
        super(cause);
    }

    public CannotChangeFileOwnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
