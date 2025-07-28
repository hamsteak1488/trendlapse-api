package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final RegionReader regionReader;
    private final TrendingCollector trendingCollector;
    private final CollectSchedulerProperties collectSchedulerProperties;

    @Value("${only-korea-region:false}")
    private boolean onlyKoreaRegion;

    @Scheduled(initialDelayString = "${collect-scheduler.initial-delay}", fixedRateString = "${collect-scheduler.collect-interval}")
    public void collect() {
        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        List<Long> regionsIds = regionReader.readAll().stream()
                .map(Region::getId)
                .toList();

        if (regionsIds.size() < 110) {
            log.warn("The length of the region list is less than 110.");
        }

        if (onlyKoreaRegion) {
            final long KOREA_REGION_ID = 77L;
            regionsIds = List.of(KOREA_REGION_ID);
        }

        trendingCollector.collect(dateTime, collectSchedulerProperties.getCollectCount(), regionsIds);
    }
}
