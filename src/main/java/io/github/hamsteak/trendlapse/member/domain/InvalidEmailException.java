package io.github.hamsteak.trendlapse.member.domain;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
