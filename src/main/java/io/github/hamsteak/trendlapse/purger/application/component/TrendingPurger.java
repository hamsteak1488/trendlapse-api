package io.github.hamsteak.trendlapse.purger.application.component;

import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class TrendingPurger {
    private final TrendingRepository trendingRepository;
    private final Duration expirationPeriod;
    private final int batchSize;

    public TrendingPurger(TrendingRepository trendingRepository,
                          @Value("${purge-scheduler.expiration-period}") Duration expirationPeriod,
                          @Value("${purge-scheduler.batch-size}") int batchSize) {
        this.trendingRepository = trendingRepository;
        this.expirationPeriod = expirationPeriod;
        this.batchSize = batchSize;
    }

    public int purge(LocalDateTime dateTime) {
        LocalDateTime expirationDateTime = dateTime.minus(expirationPeriod);

        int totalPurgedCount = 0;

        int purgedCount;
        do {
            purgedCount = trendingRepository.deleteByDateTimeBefore(expirationDateTime, batchSize);
            log.debug("Purging... deleted {} records.", purgedCount);
            totalPurgedCount += purgedCount;
        } while (purgedCount > 0);

        log.info("Purged {} expired records.", totalPurgedCount);

        return totalPurgedCount;
    }
}
