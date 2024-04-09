package dragonfly.ews.domain.filelog.controller;

import dragonfly.ews.common.handler.ErrorResponse;
import dragonfly.ews.domain.filelog.exception.CannotResolveFileReadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ExcelMemberFileLogController.class)
public class ExcelMemberFileLogControllerAdvice {
    /**
     * [회원의 파일을 읽을 수 없는 경우]
     * @param e 예외
     * @return
     */
    @ExceptionHandler
    public ResponseEntity handleException(CannotResolveFileReadException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
