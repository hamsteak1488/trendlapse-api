package io.github.hamsteak.trendlapse.member.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException(String message) {
        super(DomainError.INVALID_PASSWORD, message);
    }
}
