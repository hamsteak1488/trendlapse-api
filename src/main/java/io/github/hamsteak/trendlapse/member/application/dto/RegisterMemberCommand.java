package io.github.hamsteak.trendlapse.member.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterMemberCommand {
    @NotBlank
    private final String username;
    @NotBlank
    private final String password;
    @Email
    private final String email;
}
