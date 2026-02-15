package io.github.hamsteak.trendlapse.report.snapshot.infrastructure.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CodexApiRequest {
    private final String system;
    private final String user;
}
