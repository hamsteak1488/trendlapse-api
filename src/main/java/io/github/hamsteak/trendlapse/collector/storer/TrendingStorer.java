package io.github.hamsteak.trendlapse.collector.storer;

import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;

import java.util.List;

public interface TrendingStorer {
    int store(List<TrendingItem> trendingItems);
}
