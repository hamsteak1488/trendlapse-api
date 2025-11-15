package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingFetcher {
    List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, List<String> regionCodes, int maxResultCount);
}
