package io.github.hamsteak.trendlapse.purger.application;

import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotRepository;
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
public class PurgeExpiredTrendingVideoRankingSnapshotService {
    private final TrendingVideoRankingSnapshotRepository trendingVideoRankingSnapshotRepository;
    private final Duration expirationPeriod;
    private final int batchSize;

    public PurgeExpiredTrendingVideoRankingSnapshotService(
            TrendingVideoRankingSnapshotRepository trendingVideoRankingSnapshotRepository,
            @Value("${purge-scheduler.expiration-period}") Duration expirationPeriod,
            @Value("${purge-scheduler.batch-size}") int batchSize
    ) {
        this.trendingVideoRankingSnapshotRepository = trendingVideoRankingSnapshotRepository;
        this.expirationPeriod = expirationPeriod;
        this.batchSize = batchSize;
    }

    @Transactional
    public int purge(LocalDateTime now) {
        LocalDateTime expirationTime = now.minus(expirationPeriod);

        int totalPurgedCount = 0;
        int purgedCount;
        do {
            List<Long> snapshotIds =
                    trendingVideoRankingSnapshotRepository.findByCapturedAtBefore(expirationTime, Limit.of(batchSize)).stream()
                            .map(TrendingVideoRankingSnapshot::getId)
                            .toList();

            purgedCount = trendingVideoRankingSnapshotRepository.deleteByIdIn(snapshotIds);

            log.debug("Purging... deleted {} records.", purgedCount);
            totalPurgedCount += purgedCount;
        } while (purgedCount > 0);

        log.info("Purged {} expired records.", totalPurgedCount);

        return totalPurgedCount;
    }
}
