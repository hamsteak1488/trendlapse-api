package io.github.hamsteak.trendlapse.member.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void constructor_throws_InvalidPasswordException_when_password_blank(String password) {
        assertThatThrownBy(() ->
                new Member(null, new Username("Steve"), new Password(password), new Email("abc@gmail.com"))
        ).isInstanceOf(InvalidPasswordException.class);
    }
}