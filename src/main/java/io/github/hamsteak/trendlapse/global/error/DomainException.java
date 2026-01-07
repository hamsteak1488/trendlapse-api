package io.github.hamsteak.trendlapse.global.error;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final DomainError domainError;

    public DomainException(DomainError domainError, String message) {
        super(message);
        this.domainError = domainError;
    }

    public DomainException(DomainError domainError, String message, Throwable cause) {
        super(message, cause);
        this.domainError = domainError;
    }
}