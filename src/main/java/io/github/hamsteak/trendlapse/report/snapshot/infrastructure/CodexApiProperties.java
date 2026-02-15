package io.github.hamsteak.trendlapse.report.snapshot.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "codex-api")
@Getter
@RequiredArgsConstructor
public class CodexApiProperties {
    private final String url;
}
