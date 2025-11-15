package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.trending.application.dto.DateTimeTrendingDetailList;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingSearchFilter;

import java.util.List;

public interface TrendingSearcher {
    List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter);
}
