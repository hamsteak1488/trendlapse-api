package io.github.hamsteak.trendlapse.collector.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "collect-scheduler")
@Getter
@RequiredArgsConstructor
public class CollectSchedulerProperties {
    private final int collectCount;
    private final int collectInterval;
    private final boolean useLog;
}
