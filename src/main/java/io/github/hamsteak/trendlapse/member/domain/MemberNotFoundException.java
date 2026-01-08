package io.github.hamsteak.trendlapse.member.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class MemberNotFoundException extends DomainException {
    public MemberNotFoundException(String message) {
        super(DomainError.MEMBER_NOT_FOUND, message);
    }
}
