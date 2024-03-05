package dragonfly.ews.domain.file.exception;

public class ExtensionMismatchException extends RuntimeException {
    public ExtensionMismatchException() {
    }

    public ExtensionMismatchException(String message) {
        super(message);
    }

    public ExtensionMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionMismatchException(Throwable cause) {
        super(cause);
    }

    public ExtensionMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
