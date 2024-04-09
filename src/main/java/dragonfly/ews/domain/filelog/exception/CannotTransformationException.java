package dragonfly.ews.domain.filelog.exception;

public class CannotTransformationException extends RuntimeException {
    public CannotTransformationException() {
    }

    public CannotTransformationException(String message) {
        super(message);
    }

    public CannotTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotTransformationException(Throwable cause) {
        super(cause);
    }

    public CannotTransformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
