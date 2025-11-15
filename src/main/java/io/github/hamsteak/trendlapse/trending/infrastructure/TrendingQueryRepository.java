package io.github.hamsteak.trendlapse.trending.infrastructure;

import io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingSearchFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrendingQueryRepository {
    public List<TrendingDetail> getTrendings(TrendingSearchFilter filter) {
        return null;
    }
}
