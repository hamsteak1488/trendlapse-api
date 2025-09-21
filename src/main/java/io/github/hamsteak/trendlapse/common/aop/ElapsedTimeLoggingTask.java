package io.github.hamsteak.trendlapse.common.aop;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ElapsedTimeLoggingTask {
    private final String pointcut;
    private final String name;
    private final boolean enabled = true;
}
