package io.github.hamsteak.trendlapse.member.domain;

import org.apache.commons.validator.routines.EmailValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTest {
    @Test
    void isValid_validate_email() {
        // given
        String validEmail = "abc@gmail.com";
        String invalidEmail = "abcgmailcom";

        // when
        boolean valid = EmailValidator.getInstance().isValid(validEmail);
        boolean invalid = EmailValidator.getInstance().isValid(invalidEmail);

        // then
        assertThat(valid).isTrue();
        Assertions.assertThat(invalid).isFalse();
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"abcgmailcom", "abcgmail.com", "abc@gmailcom"})
    void constructor_throws_InvalidEmailException_when_email_invalid(String email) {
        // when
        Throwable thrown = Assertions.catchThrowable(() -> new Member(null, new Username("Steve"), new Password("1234"), new Email(email)));

        // then
        assertThat(thrown).isInstanceOf(InvalidEmailException.class);
    }
}