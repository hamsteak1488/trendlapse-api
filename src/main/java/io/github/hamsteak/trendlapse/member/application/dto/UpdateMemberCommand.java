package io.github.hamsteak.trendlapse.member.application.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateMemberCommand {
    private final String username;
    private final String password;
    @Email
    private final String email;
}
