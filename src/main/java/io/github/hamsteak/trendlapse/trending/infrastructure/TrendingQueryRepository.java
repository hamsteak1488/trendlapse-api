package io.github.hamsteak.trendlapse.trending.infrastructure;

import io.github.hamsteak.trendlapse.trending.domain.TrendingDetail;
import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrendingQueryRepository {
    public List<TrendingDetail> getTrendings(TrendingSearchFilter filter) {
        return null;
    }
}
