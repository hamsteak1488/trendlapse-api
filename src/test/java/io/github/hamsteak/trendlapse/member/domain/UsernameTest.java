package io.github.hamsteak.trendlapse.member.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsernameTest {
    @Test
    void of_creates_username_when_valid() {
        Username username = Username.of("Steve");
        assertThat(username.getValue()).isEqualTo("Steve");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void of_throws_InvalidUsernameException_when_blank(String username) {
        assertThatThrownBy(() -> Username.of(username))
                .isInstanceOf(InvalidUsernameException.class);
    }
}