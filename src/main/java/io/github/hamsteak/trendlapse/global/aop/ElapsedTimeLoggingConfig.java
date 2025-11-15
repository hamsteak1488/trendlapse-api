package io.github.hamsteak.trendlapse.global.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElapsedTimeLoggingConfig {
    private final ElapsedTimeLoggingProperties elapsedTimeLoggingProperties;

    @Bean
    @ConditionalOnProperty(prefix = "elapsed-time-logging", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor elapsedTimeLoggingPostProcessor() {
        return new ElapsedTimeLoggingPostProcessor(elapsedTimeLoggingProperties.getTasks());
    }
}
