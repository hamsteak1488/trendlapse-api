package io.github.hamsteak.trendlapse.member.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Email {
    private String value;

    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email must not be blank.");
        }
        if (!EmailValidator.getInstance().isValid(value)) {
            throw new InvalidEmailException("Email is invalid.");
        }

        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
