package io.github.hamsteak.trendlapse.member.domain;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
