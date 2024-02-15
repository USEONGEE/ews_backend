package dragonfly.ews.domain.filelog.exception;

public class CannotResolveFileReadException extends RuntimeException {
    public CannotResolveFileReadException() {
    }

    public CannotResolveFileReadException(String message) {
        super(message);
    }

    public CannotResolveFileReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotResolveFileReadException(Throwable cause) {
        super(cause);
    }

    public CannotResolveFileReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
