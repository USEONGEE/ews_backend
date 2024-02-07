package dragonfly.ews.domain.file.exception;

/**
 * [파일을 정상적으로 저장할 수 없을 때 발생하는 예외]
 * <p/>IOException을 변환하는 에외
 */
public class CannotSaveFileException extends RuntimeException {
    public CannotSaveFileException() {
    }

    public CannotSaveFileException(String message) {
        super(message);
    }

    public CannotSaveFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotSaveFileException(Throwable cause) {
        super(cause);
    }

    public CannotSaveFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
