package dragonfly.ews.domain.file.exception;

public class FileNotInProjectException extends RuntimeException {
    public FileNotInProjectException() {
    }

    public FileNotInProjectException(String message) {
        super(message);
    }

    public FileNotInProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotInProjectException(Throwable cause) {
        super(cause);
    }

    public FileNotInProjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
