package dragonfly.ews.domain.result.exceptioon;

public class CannotProcessAnalysisException extends RuntimeException {
    public CannotProcessAnalysisException() {
    }

    public CannotProcessAnalysisException(String message) {
        super(message);
    }

    public CannotProcessAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotProcessAnalysisException(Throwable cause) {
        super(cause);
    }

    public CannotProcessAnalysisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
