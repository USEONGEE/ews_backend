package dragonfly.ews.domain.file.exception;

/**
 * 파일명에서 확장자를 찾을 수 없을 때 발생하는 예외
 */
public class ExtensionNotFoundException extends RuntimeException {
    public ExtensionNotFoundException() {
    }

    public ExtensionNotFoundException(String message) {
        super(message);
    }

    public ExtensionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionNotFoundException(Throwable cause) {
        super(cause);
    }

    public ExtensionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
