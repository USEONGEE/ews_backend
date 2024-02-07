package dragonfly.ews.domain.file.exception;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException() {
    }

    public NoSuchFileException(String message) {
        super(message);
    }

    public NoSuchFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchFileException(Throwable cause) {
        super(cause);
    }

    public NoSuchFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
