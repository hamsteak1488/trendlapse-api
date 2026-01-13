package io.github.hamsteak.trendlapse.member.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberView {
    private final String username;
    private final String email;
}
