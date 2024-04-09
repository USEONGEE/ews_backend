package dragonfly.ews.domain.base;

import dragonfly.ews.common.handler.ErrorResponse;
import dragonfly.ews.domain.file.exception.CannotSaveFileException;
import dragonfly.ews.domain.file.exception.ExtensionMismatchException;
import dragonfly.ews.domain.file.exception.NoSuchFileException;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import dragonfly.ews.domain.project.exception.NoSuchProjectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseControllerAdvice {
    /**
     * [파일 저장에 실패했을 때 발생하는 예외 처리]
     * @param e
     * @return
     */
    @ExceptionHandler
    public ResponseEntity handleException(CannotSaveFileException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity handleException(NoSuchMemberException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity handleException(NoSuchFileException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity handleException(NoSuchProjectException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * [파일 확장자가 맞지 않을 때 발생하는 예외 처리]
     * <p/> - 파일 확장자가 맞지 않을 때 발생하는 예외 처리
     * @param e
     */
    @ExceptionHandler
    public ResponseEntity handleException(ExtensionMismatchException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }


}
