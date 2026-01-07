package io.github.hamsteak.trendlapse.member.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class InvalidUsernameException extends DomainException {
    public InvalidUsernameException(String message) {
        super(DomainError.INVALID_USERNAME, message);
    }
}
