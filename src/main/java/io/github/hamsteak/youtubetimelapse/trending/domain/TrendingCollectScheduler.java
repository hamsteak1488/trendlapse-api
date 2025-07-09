package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.config.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class TrendingCollectScheduler {
    private final TrendingCollector trendingCollector;
    private final int collectCount;

    public TrendingCollectScheduler(TrendingCollector trendingCollector, @Value("${collect-count}") int collectCount) {
        this.trendingCollector = trendingCollector;
        this.collectCount = collectCount;
    }

    @Scheduled(fixedDelay = Constants.COLLECT_INTERVAL)
    public void collect() {
        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());

        trendingCollector.collect(dateTime, collectCount);
    }
}
