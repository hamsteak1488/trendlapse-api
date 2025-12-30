package io.github.hamsteak.trendlapse.global.logelapsedtime.config;


import lombok.Getter;

@Getter
public class ElapsedTimeLoggingTask {
    private final String pointcut;
    private final String name;
    private final boolean disabled;
    private final boolean loggingBeanNameEnabled;

    public ElapsedTimeLoggingTask(String pointcut, String name, boolean disabled, boolean loggingBeanNameEnabled) {
        this.pointcut = pointcut;
        this.name = name;
        this.disabled = disabled;
        this.loggingBeanNameEnabled = loggingBeanNameEnabled;
    }
}
