package io.github.hamsteak.trendlapse.member.domain;

import org.springframework.stereotype.Component;

@Component
public class WeakPasswordPolicy implements PasswordPolicy {
    @Override
    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidPasswordException("Password must not be blank.");
        }
        if (rawPassword.length() < 4) {
            throw new InvalidPasswordException("Password length must be more than 4.");
        }
    }
}
