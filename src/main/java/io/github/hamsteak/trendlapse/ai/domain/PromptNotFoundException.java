package io.github.hamsteak.trendlapse.ai.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class PromptNotFoundException extends DomainException {
    public PromptNotFoundException(String message) {
        super(DomainError.PROMPT_NOT_FOUND, message);
    }
}
