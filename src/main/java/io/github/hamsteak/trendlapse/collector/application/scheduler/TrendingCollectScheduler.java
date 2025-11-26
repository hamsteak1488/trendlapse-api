package io.github.hamsteak.trendlapse.collector.application.scheduler;

import io.github.hamsteak.trendlapse.collector.application.service.TrendingCollectService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final TrendingCollectService trendingCollectService;

    @Timed("collect.whole")
    @Scheduled(cron = "${collect-scheduler.collect-cron}", zone = "UTC")
    public void collect() {
        log.info("Starting scheduled trending collection job.");

        trendingCollectService.collect();

        log.info("Completed scheduled trending collection job.");
    }
}
