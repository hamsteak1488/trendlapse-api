package io.github.hamsteak.trendlapse.collector.application;

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
    private final int collectSize;

    private final boolean useLog;
}
