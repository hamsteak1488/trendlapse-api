package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.domain.RegionFetcher;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final RegionFetcher regionFetcher;
    private final RegionReader regionReader;
    private final TrendingCollector trendingCollector;
    private final CollectSchedulerProperties collectSchedulerProperties;

    @Scheduled(fixedDelayString = "${collect-scheduler.collect-interval}")
    public void collect() {
        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        regionFetcher.fetch();
        List<Long> regionsIds = regionReader.readAll().stream()
                .map(Region::getId)
                .toList();

        final long KOREA_REGION_ID = 77L;

        trendingCollector.collect(dateTime, collectSchedulerProperties.getCollectCount(), List.of(KOREA_REGION_ID));
    }
}
