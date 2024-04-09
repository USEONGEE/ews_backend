package dragonfly.ews.domain.member.controller;

import dragonfly.ews.common.handler.ErrorResponse;
import dragonfly.ews.domain.member.exception.NoSuchMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MemberController.class)
public class
MemberControllerAdvice {

    @ExceptionHandler
    public ResponseEntity handleException(IllegalArgumentException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handleException(NoSuchMemberException e) {
        return new ResponseEntity(ErrorResponse.of(0, e.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity handleException(Exception e) {
        return new ResponseEntity(ErrorResponse.of(-1, e.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
