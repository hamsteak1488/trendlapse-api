package io.github.hamsteak.trendlapse.common.aop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "elapsed-time-logging")
@Getter
@RequiredArgsConstructor
public class ElapsedTimeLoggingProperties {
    private final List<ElapsedTimeLoggingTask> tasks = new ArrayList<>();
}
