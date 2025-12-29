package io.github.hamsteak.trendlapse.global.logelapsedtime.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "elapsed-time-logging", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class ElapsedTimeLoggingConfig {
    @Bean
    public static BeanPostProcessor elapsedTimeLoggingPostProcessor(ElapsedTimeLoggingProperties elapsedTimeLoggingProperties) {
        return new ElapsedTimeLoggingPostProcessor(elapsedTimeLoggingProperties);
    }
}
