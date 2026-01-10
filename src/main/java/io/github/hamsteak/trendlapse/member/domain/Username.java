package io.github.hamsteak.trendlapse.member.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Username {
    private String value;

    private Username(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUsernameException("Username must not be blank.");
        }
        this.value = value;
    }

    public static Username of(String value) {
        return new Username(value);
    }
}
