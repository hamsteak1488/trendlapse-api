package io.github.hamsteak.trendlapse.member.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class DuplicateUsernameException extends DomainException {
    public DuplicateUsernameException(String message) {
        super(DomainError.DUPLICATE_USERNAME, message);
    }
}
