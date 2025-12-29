package io.github.hamsteak.trendlapse.collector.infrastructure;

import io.github.hamsteak.trendlapse.collector.application.CollectTrendingSnapshotService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final CollectTrendingSnapshotService collectTrendingSnapshotService;

    @Timed("collect.whole")
//    @Scheduled(cron = "${collect-scheduler.collect-cron}", zone = "UTC")
//    @Scheduled(fixedDelay = 30 * 60 * 1000, zone = "UTC")
    public void collect() {
        log.info("Starting scheduled trending collection job.");

        collectTrendingSnapshotService.collect(LocalDateTime.now(Clock.systemUTC()));

        log.info("Completed scheduled trending collection job.");
    }
}
