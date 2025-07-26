package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.region.domain.RegionFetcher;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final RegionReader regionReader;
    private final TrendingCollector trendingCollector;
    private final CollectSchedulerProperties collectSchedulerProperties;
    private final RegionFetcher regionFetcher;

    @Value(value = "${only-korea-region:false}")
    private boolean onlyKoreaRegion;

    @Scheduled(fixedDelayString = "${collect-scheduler.collect-interval}")
    public void collect() {
        regionFetcher.fetch();

        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());

        if (onlyKoreaRegion) {
            final long REGION_KOREA = 77L;
            trendingCollector.collect(dateTime, collectSchedulerProperties.getCollectCount(), REGION_KOREA);
            return;
        }

        regionReader.readAll()
                .forEach(region ->
                        trendingCollector.collect(dateTime, collectSchedulerProperties.getCollectCount(), region.getId())
                );
    }
}
