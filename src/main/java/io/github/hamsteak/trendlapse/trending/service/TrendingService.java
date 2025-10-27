package io.github.hamsteak.trendlapse.trending.service;

import io.github.hamsteak.trendlapse.trending.domain.*;
import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;
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

    public List<DateTimeTrendingDetailList> searchTrendingBatchSize(TrendingSearchFilter filter) {
        return trendingSearcherV0.search(filter);
    }

    public List<DateTimeTrendingDetailList> searchTrendingFetchJoin(TrendingSearchFilter filter) {
        return trendingSearcherVFetchJoin.search(filter);
    }

    public List<DateTimeTrendingDetailList> searchTrendingJoinDTO(TrendingSearchFilter filter) {
        return trendingSearcherV1.search(filter);
    }
}
