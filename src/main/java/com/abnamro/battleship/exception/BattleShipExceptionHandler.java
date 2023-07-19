package com.abnamro.battleship.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class BattleShipExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return getErrorResponseResponseEntity(String.join(", ", errorMessages));
    }

    @ExceptionHandler(InvalidShipDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidShipDataException(InvalidShipDataException ex){
        return getErrorResponseResponseEntity(ex.getMessage());
    }

    @ExceptionHandler(InvalidGameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidGameIdException(InvalidGameException ex){
        return getErrorResponseResponseEntity(ex.getMessage());
    }

    private static ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(String errorMessages) {
        ErrorResponse errorResponse = getErrorResponse(errorMessages);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private static ErrorResponse getErrorResponse(String errorMessage) {
        log.error("Exception occurred with msg :: {}", errorMessage);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(errorMessage);
        return errorResponse;
    }
}
