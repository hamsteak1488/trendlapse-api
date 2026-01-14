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
    private String hashValue;

    private Password(String hashValue) {
        if (hashValue == null || hashValue.isBlank()) {
            throw new InvalidPasswordException("Password must not be blank.");
        }
        this.hashValue = hashValue;
    }

    public static Password of(String passwordHash) {
        return new Password(passwordHash);
    }
}
