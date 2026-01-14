package io.github.hamsteak.trendlapse.member.domain;

public interface PasswordPolicy {
    void validate(String rawPassword);
}
