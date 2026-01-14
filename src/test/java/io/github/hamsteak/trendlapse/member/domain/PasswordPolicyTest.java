package io.github.hamsteak.trendlapse.member.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PasswordPolicyTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1", "12", "123"})
    void validate_throw_InvalidPasswordException_when_password_invalid(String rawPassword) {
        // given
        PasswordPolicy policy = new WeakPasswordPolicy();

        // when
        Throwable thrown = catchThrowable(() -> policy.validate(rawPassword));

        // then
        assertThat(thrown).isInstanceOf(InvalidPasswordException.class);
    }
}