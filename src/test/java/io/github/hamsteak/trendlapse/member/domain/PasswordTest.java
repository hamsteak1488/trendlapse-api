package io.github.hamsteak.trendlapse.member.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {
    @Test
    void of_creates_password_when_valid() {
        Password password = Password.of("1234");
        assertThat(password.getHashValue()).isEqualTo("1234");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void of_throws_InvalidPasswordException_when_blank(String password) {
        assertThatThrownBy(() -> Password.of(password))
                .isInstanceOf(InvalidPasswordException.class);
    }
}