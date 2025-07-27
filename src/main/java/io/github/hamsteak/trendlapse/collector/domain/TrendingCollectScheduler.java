package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.region.domain.RegionFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final RegionFetcher regionFetcher;
    private final TrendingCollector trendingCollector;
    private final CollectSchedulerProperties collectSchedulerProperties;

    @Scheduled(fixedDelayString = "${collect-scheduler.collect-interval}")
    public void collect() {
        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        regionFetcher.fetch();

        trendingCollector.collect(dateTime, collectSchedulerProperties.getCollectCount());
    }
}
