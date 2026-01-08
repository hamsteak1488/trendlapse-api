package io.github.hamsteak.trendlapse.member.domain;

import org.apache.commons.validator.routines.EmailValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {
    private static final String DEFAULT_USERNAME = "Steve";
    private static final String DEFAULT_PASSWORD = "1234";
    private static final String DEFAULT_EMAIL = "abc@gmail.com";

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void constructor_throws_InvalidUsernameException_when_username_blank(String username) {
        // when
        Throwable thrown = Assertions.catchThrowable(() -> new Member(null, username, DEFAULT_PASSWORD, DEFAULT_EMAIL));

        // then
        assertThat(thrown).isInstanceOf(InvalidUsernameException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void constructor_throws_InvalidPasswordException_when_password_blank(String password) {
        // when
        Throwable thrown = Assertions.catchThrowable(() -> new Member(null, DEFAULT_USERNAME, password, DEFAULT_EMAIL));

        // then
        assertThat(thrown).isInstanceOf(InvalidPasswordException.class);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"abcgmailcom", "abcgmail.com", "abc@gmailcom"})
    void constructor_throws_InvalidEmailException_when_email_invalid(String email) {
        // when
        Throwable thrown = Assertions.catchThrowable(() -> new Member(null, DEFAULT_USERNAME, DEFAULT_PASSWORD, email));

        // then
        assertThat(thrown).isInstanceOf(InvalidEmailException.class);
    }

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
}
