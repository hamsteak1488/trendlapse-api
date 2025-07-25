package io.github.hamsteak.trendlapse.collector.domain;

import java.time.LocalDateTime;

public interface TrendingCollector {
    void collect(LocalDateTime dateTime, int collectCount, long regionId);
}
