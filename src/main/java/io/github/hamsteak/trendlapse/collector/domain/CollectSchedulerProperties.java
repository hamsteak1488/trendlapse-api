package io.github.hamsteak.trendlapse.collector.domain;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "collect-scheduler")
@Validated
@Getter
@RequiredArgsConstructor
public class CollectSchedulerProperties {
    @Range(min = 0, max = 200)
    private final int collectCount;

    @Min(60 * 1000)
    private final int collectInterval;

    private final boolean useLog;
}
