package dragonfly.ews.domain.file.controller;

import dragonfly.ews.common.handler.ErrorResponse;
import dragonfly.ews.domain.result.exceptioon.CannotProcessAnalysisException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MemberFileController.class)
public class MemberFileControllerAdvice {
    @ExceptionHandler
    public ResponseEntity handleException(IllegalArgumentException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    /**
     * [분석 서버 요청 실패]
     * <p/> - 분석 서버 요청 실패 시 처리
     * <p/> - {@link dragonfly.ews.domain.file.aop.postprocessor.ColumnTypeCheckPostProcessor} 참조
     * @param e
     * @return
     */
    @ExceptionHandler
    public ResponseEntity handleException(CannotProcessAnalysisException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
