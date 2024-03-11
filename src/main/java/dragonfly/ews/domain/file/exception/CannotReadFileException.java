package dragonfly.ews.domain.file.exception;

public class CannotReadFileException extends RuntimeException {
    public CannotReadFileException() {
    }

    public CannotReadFileException(String message) {
        super(message);
    }

    public CannotReadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotReadFileException(Throwable cause) {
        super(cause);
    }

    public CannotReadFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
