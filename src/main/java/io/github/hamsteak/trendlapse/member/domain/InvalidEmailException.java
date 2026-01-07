package io.github.hamsteak.trendlapse.member.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class InvalidEmailException extends DomainException {
    public InvalidEmailException(String message) {
        super(DomainError.INVALID_EMAIL, message);
    }
}
