package io.github.hamsteak.trendlapse.member.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {
    @Test
    void of_creates_email_when_valid() {
        Email email = Email.of("abc@gmail.com");
        assertThat(email.getValue()).isEqualTo("abc@gmail.com");
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n", "abcgmailcom", "abcgmail.com", "abc@gmailcom"})
    void of_throws_InvalidEmailException_when_invalid(String email) {
        // Null value is valid.
        // when
        assertThatThrownBy(() -> Email.of(email))
                .isInstanceOf(InvalidEmailException.class);
    }
}