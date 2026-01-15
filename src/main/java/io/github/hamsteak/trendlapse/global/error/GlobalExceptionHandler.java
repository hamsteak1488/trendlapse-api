package io.github.hamsteak.trendlapse.global.error;

import io.github.hamsteak.trendlapse.member.web.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static HttpStatus mapStatus(DomainError domainError) {
        return switch (domainError) {
            case INVALID_USERNAME, INVALID_PASSWORD, INVALID_EMAIL -> HttpStatus.BAD_REQUEST;
            case DUPLICATE_USERNAME -> HttpStatus.CONFLICT;
            case MEMBER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case LOGIN_FAILED -> HttpStatus.BAD_REQUEST;
        };
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException e) {
        HttpStatus httpStatus = mapStatus(e.getDomainError());
        return ResponseEntity.status(httpStatus)
                .body(makeErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(makeErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(makeErrorResponse("Method arguments are not valid.", e.getFieldErrors()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected exception occured.", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(makeErrorResponse(e.getMessage()));
    }

    private ErrorResponse makeErrorResponse(String message) {
        return new ErrorResponse(message);
    }

    private ErrorResponse makeErrorResponse(String message, List<FieldError> fieldErrors) {
        return new ErrorResponse(message, fieldErrors);
    }
}