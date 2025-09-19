package io.github.hamsteak.trendlapse.collector.fetcher;

import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingFetcher {
    List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, List<String> regionCodes, int maxResultCount);
}
