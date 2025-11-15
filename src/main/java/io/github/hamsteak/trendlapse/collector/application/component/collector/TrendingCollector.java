package io.github.hamsteak.trendlapse.collector.application.component.collector;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingCollector {
    void collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes);
}
