package io.github.hamsteak.trendlapse.member.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class LoginFailedException extends DomainException {
    public LoginFailedException(String message) {
        super(DomainError.LOGIN_FAILED, message);
    }
}
