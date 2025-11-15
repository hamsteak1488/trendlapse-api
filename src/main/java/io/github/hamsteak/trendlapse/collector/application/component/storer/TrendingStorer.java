package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;

import java.util.List;

public interface TrendingStorer {
    int store(List<TrendingItem> trendingItems);
}
