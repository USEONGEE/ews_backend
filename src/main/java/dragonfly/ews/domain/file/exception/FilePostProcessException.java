package dragonfly.ews.domain.file.exception;

public class FilePostProcessException extends RuntimeException {
    public FilePostProcessException() {
    }

    public FilePostProcessException(String message) {
        super(message);
    }

    public FilePostProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilePostProcessException(Throwable cause) {
        super(cause);
    }

    public FilePostProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
