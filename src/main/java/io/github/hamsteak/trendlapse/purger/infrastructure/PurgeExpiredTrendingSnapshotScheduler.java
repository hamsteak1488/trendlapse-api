package io.github.hamsteak.trendlapse.purger.infrastructure;

import io.github.hamsteak.trendlapse.purger.application.PurgeExpiredTrendingSnapshotService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class PurgeExpiredTrendingSnapshotScheduler {
    private final PurgeExpiredTrendingSnapshotService purgeExpiredTrendingSnapshotService;

    @Timed("purge.whole")
    @Scheduled(cron = "${purge-scheduler.purge-cron}", zone = "UTC")
    public void purge() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        purgeExpiredTrendingSnapshotService.purge(now);
    }
}
