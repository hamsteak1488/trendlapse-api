package io.github.hamsteak.trendlapse.purger.application;

import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshot;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    public int purge(LocalDateTime now) {
        LocalDateTime expirationTime = now.minus(expirationPeriod);

        int totalPurgedCount = 0;
        int purgedCount;
        do {
            List<Long> expiredTrendingSnapshotIds =
                    trendingSnapshotRepository.findByCapturedAtBefore(expirationTime, Limit.of(batchSize)).stream()
                            .map(TrendingSnapshot::getId)
                            .toList();

            purgedCount = trendingSnapshotRepository.deleteByIdIn(expiredTrendingSnapshotIds);

            log.debug("Purging... deleted {} records.", purgedCount);
            totalPurgedCount += purgedCount;
        } while (purgedCount > 0);

        log.info("Purged {} expired records.", totalPurgedCount);

        return totalPurgedCount;
    }
}
