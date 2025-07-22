package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.config.Constants;
import io.github.hamsteak.youtubetimelapse.region.domain.RegionFetcher;
import io.github.hamsteak.youtubetimelapse.region.domain.RegionReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class TrendingCollectScheduler {
    private final RegionFetcher regionFetcher;
    private final RegionReader regionReader;
    private final TrendingCollector trendingCollector;
    private final int collectCount;

    public TrendingCollectScheduler(
            RegionFetcher regionFetcher,
            RegionReader regionReader,
            TrendingCollector trendingCollector,
            @Value("${collect-count}") int collectCount
    ) {
        this.regionFetcher = regionFetcher;
        this.regionReader = regionReader;
        this.trendingCollector = trendingCollector;
        this.collectCount = collectCount;
    }

    @Scheduled(fixedDelay = Constants.COLLECT_INTERVAL)
    public void collect() {
        regionFetcher.fetch();

        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        trendingCollector.collect(dateTime, collectCount, 77L);

//        regionReader.readAll()
//                        .forEach(region ->
//                                trendingCollector.collect(dateTime, collectCount, region.getId())
//                        );
    }
}
