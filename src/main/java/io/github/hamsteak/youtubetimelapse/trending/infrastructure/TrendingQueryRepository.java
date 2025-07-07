package io.github.hamsteak.youtubetimelapse.trending.infrastructure;

import io.github.hamsteak.youtubetimelapse.trending.domain.TrendingDetail;
import io.github.hamsteak.youtubetimelapse.trending.domain.dto.TrendingSearchFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrendingQueryRepository {
    public List<TrendingDetail> getTrendings(TrendingSearchFilter filter) {
        return null;
    }
}
