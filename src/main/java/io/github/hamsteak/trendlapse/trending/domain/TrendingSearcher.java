package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;

import java.util.List;

public interface TrendingSearcher {
    List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter);
}
