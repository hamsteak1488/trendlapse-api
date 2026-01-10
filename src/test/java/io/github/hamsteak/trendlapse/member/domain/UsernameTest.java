package io.github.hamsteak.trendlapse.member.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UsernameTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void constructor_throws_InvalidUsernameException_when_username_blank(String username) {
        Assertions.assertThatThrownBy(() -> Username.of(username))
                .isInstanceOf(InvalidUsernameException.class);
    }
}