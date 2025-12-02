package io.github.hamsteak.trendlapse.global.querycounter;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBooleanProperty(name = "query-counter")
@Configuration
public class QueryCounterDataSourceProxyConfig {
    @Bean
    public static QueryCounterDataSourceProxyPostProcessor queryCounterDataSourceProxyPostProcessor(ObjectProvider<MeterRegistry> meterRegistryProvider) {
        return new QueryCounterDataSourceProxyPostProcessor(meterRegistryProvider);
    }
}
