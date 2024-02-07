package dragonfly.ews.domain.file.exception;

/**
 * [클라이언트가 제공한 파일의 이름이 없을 때 발생하는 에외]
 */
public class NoFileNameException extends RuntimeException {
    public NoFileNameException() {
    }

    public NoFileNameException(String message) {
        super(message);
    }

    public NoFileNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoFileNameException(Throwable cause) {
        super(cause);
    }

    public NoFileNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
