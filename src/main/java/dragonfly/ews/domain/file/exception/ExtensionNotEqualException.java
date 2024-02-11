package dragonfly.ews.domain.file.exception;

public class ExtensionNotEqualException extends RuntimeException {

    public ExtensionNotEqualException() {
    }

    public ExtensionNotEqualException(String message) {
        super(message);
    }

    public ExtensionNotEqualException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionNotEqualException(Throwable cause) {
        super(cause);
    }

    public ExtensionNotEqualException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
