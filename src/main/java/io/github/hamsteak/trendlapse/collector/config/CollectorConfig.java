package io.github.hamsteak.trendlapse.collector.config;

import io.github.hamsteak.trendlapse.collector.domain.CollectSchedulerProperties;
import io.github.hamsteak.trendlapse.collector.domain.TrendingCollectScheduler;
import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectorConfig {
    @Bean
    public TrendingCollectScheduler trendingCollectScheduler(
            RegionReader regionReader,
            @Qualifier("batchQueueTrendingCollector") TrendingCollector trendingCollector,
            CollectSchedulerProperties collectSchedulerProperties
    ) {
        return new TrendingCollectScheduler(regionReader, trendingCollector, collectSchedulerProperties);
    }
}
