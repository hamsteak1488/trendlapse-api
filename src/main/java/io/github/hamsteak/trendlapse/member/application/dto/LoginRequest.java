package io.github.hamsteak.trendlapse.member.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    @NotBlank
    private final String username;
    @NotBlank
    private final String password;
}
