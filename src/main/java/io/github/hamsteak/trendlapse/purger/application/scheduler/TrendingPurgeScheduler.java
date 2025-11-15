package io.github.hamsteak.trendlapse.purger.application.scheduler;

import io.github.hamsteak.trendlapse.purger.application.component.TrendingPurger;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendingPurgeScheduler {
    private final TrendingPurger trendingPurger;

    @Timed("purge.whole")
    @Scheduled(cron = "${purge-scheduler.purge-cron}", zone = "UTC")
    public void purge() {
        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        trendingPurger.purge(dateTime);
    }
}
