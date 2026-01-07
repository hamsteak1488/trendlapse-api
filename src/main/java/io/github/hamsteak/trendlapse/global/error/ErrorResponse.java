package io.github.hamsteak.trendlapse.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
public class ErrorResponse {
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldReason> errors;

    public ErrorResponse(String message) {
        this.message = message;
        this.errors = null;
    }

    public ErrorResponse(String message, List<FieldError> fieldErrors) {
        this.message = message;
        this.errors = fieldErrors.stream()
                .map(fieldError -> new FieldReason(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
    }

    @Getter
    @RequiredArgsConstructor
    public static class FieldReason {
        private final String field;
        private final String reaseon;
    }
}
