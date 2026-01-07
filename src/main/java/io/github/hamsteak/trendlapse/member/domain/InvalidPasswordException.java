package io.github.hamsteak.trendlapse.member.domain;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
