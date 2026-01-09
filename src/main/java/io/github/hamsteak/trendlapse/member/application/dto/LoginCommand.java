package io.github.hamsteak.trendlapse.member.application.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class LoginCommand {
    private final String username;
    private final String password;
}
