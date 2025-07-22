package io.github.hamsteak.youtubetimelapse.trending.domain;

import java.time.LocalDateTime;

public interface TrendingCollector {
    void collect(LocalDateTime dateTime, int collectCount, long regionId);
}
