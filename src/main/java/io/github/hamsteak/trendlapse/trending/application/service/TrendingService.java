package io.github.hamsteak.trendlapse.trending.application.service;

import io.github.hamsteak.trendlapse.trending.application.component.TrendingSearcher;
import io.github.hamsteak.trendlapse.trending.application.component.TrendingSearcherV0;
import io.github.hamsteak.trendlapse.trending.application.component.TrendingSearcherV1;
import io.github.hamsteak.trendlapse.trending.application.component.TrendingSearcherVFetchJoin;
import io.github.hamsteak.trendlapse.trending.application.dto.DateTimeTrendingDetailList;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingSearchFilter;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendingService {
    private final TrendingSearcher trendingSearcher;
    private final TrendingSearcherV0 trendingSearcherV0;
    private final TrendingSearcherVFetchJoin trendingSearcherVFetchJoin;
    private final TrendingSearcherV1 trendingSearcherV1;

    @Timed("trending.search")
    public List<DateTimeTrendingDetailList> searchTrending(TrendingSearchFilter filter) {
        return trendingSearcher.search(filter);
    }

    @Timed("trending.search")
    public List<DateTimeTrendingDetailList> searchTrendingBatchSize(TrendingSearchFilter filter) {
        return trendingSearcherV0.search(filter);
    }

    @Timed("trending.search")
    public List<DateTimeTrendingDetailList> searchTrendingFetchJoin(TrendingSearchFilter filter) {
        return trendingSearcherVFetchJoin.search(filter);
    }

    @Timed("trending.search")
    public List<DateTimeTrendingDetailList> searchTrendingJoinDTO(TrendingSearchFilter filter) {
        return trendingSearcherV1.search(filter);
    }
}
