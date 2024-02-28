package dragonfly.ews.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception e) {
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
//        ErrorCode errorCode;
//
//        if ((errorCode = ErrorCode.getErrorCodeByErrorMessage(e.getMessage())) != null) {
//            code = errorCode.getHttpStatus();
//        }

        log.warn("[WARN] " + e.getMessage());

        return ResponseEntity.status(code)
                .body(ErrorResponse.of(code, "오류가 발생하였습니다.", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[ERROR]", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "오류가 발생하였습니다.", e.getMessage()));
    }
}
