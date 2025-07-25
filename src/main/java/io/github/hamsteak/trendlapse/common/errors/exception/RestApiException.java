package io.github.hamsteak.trendlapse.common.errors.exception;

import io.github.hamsteak.trendlapse.common.errors.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public RestApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public RestApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RestApiException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public RestApiException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}