package io.github.hamsteak.trendlapse.purger.application;

import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Slf4j
public class PurgeExpiredTrendingSnapshotService {
    private final TrendingSnapshotRepository trendingSnapshotRepository;
    private final Duration expirationPeriod;
    private final int batchSize;

    public PurgeExpiredTrendingSnapshotService(TrendingSnapshotRepository trendingSnapshotRepository,
                                               @Value("${purge-scheduler.expiration-period}") Duration expirationPeriod,
                                               @Value("${purge-scheduler.batch-size}") int batchSize) {
        this.trendingSnapshotRepository = trendingSnapshotRepository;
        this.expirationPeriod = expirationPeriod;
        this.batchSize = batchSize;
    }

    public void purge(LocalDateTime now) {
        LocalDateTime expirationTime = now.minus(expirationPeriod);

        int totalPurgedCount = 0;
        int purgedCount;
        do {
            purgedCount = trendingSnapshotRepository.deleteByDateTimeBefore(expirationTime, batchSize);
            log.debug("Purging... deleted {} records.", purgedCount);
            totalPurgedCount += purgedCount;
        } while (purgedCount > 0);

        log.info("Purged {} expired records.", totalPurgedCount);
    }
}
