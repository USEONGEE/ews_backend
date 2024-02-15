package dragonfly.ews.domain.project.exception;

public class NoSuchProjectException extends RuntimeException {
    public NoSuchProjectException() {
    }

    public NoSuchProjectException(String message) {
        super(message);
    }

    public NoSuchProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchProjectException(Throwable cause) {
        super(cause);
    }

    public NoSuchProjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
