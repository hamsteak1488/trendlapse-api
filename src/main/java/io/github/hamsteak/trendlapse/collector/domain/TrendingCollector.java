package io.github.hamsteak.trendlapse.collector.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingCollector {
    int collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes);
}
