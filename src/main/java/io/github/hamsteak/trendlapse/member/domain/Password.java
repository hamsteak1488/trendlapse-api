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
public class Password {
    private String value;

    private Password(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidPasswordException("Password must not be blank.");
        }
        this.value = value;
    }

    public static Password of(String value) {
        return new Password(value);
    }
}
