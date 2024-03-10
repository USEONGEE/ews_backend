package dragonfly.ews.domain.filelog.exception;

public class NoSuchMemberFileLogException extends RuntimeException {
    public NoSuchMemberFileLogException() {
    }

    public NoSuchMemberFileLogException(String message) {
        super(message);
    }

    public NoSuchMemberFileLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchMemberFileLogException(Throwable cause) {
        super(cause);
    }

    public NoSuchMemberFileLogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
