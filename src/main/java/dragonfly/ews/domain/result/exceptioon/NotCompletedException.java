package dragonfly.ews.domain.result.exceptioon;

public class NotCompletedException extends RuntimeException {
    public NotCompletedException() {
    }

    public NotCompletedException(String message) {
        super(message);
    }

    public NotCompletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotCompletedException(Throwable cause) {
        super(cause);
    }

    public NotCompletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
