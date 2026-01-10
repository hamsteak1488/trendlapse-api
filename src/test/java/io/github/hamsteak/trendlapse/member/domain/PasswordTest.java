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
                new Member(null, Username.of("Steve"), Password.of(password), Email.of("abc@gmail.com"))
        ).isInstanceOf(InvalidPasswordException.class);
    }
}