package io.github.hamsteak.youtubetimelapse.trending.service;

import io.github.hamsteak.youtubetimelapse.trending.domain.DateTimeTrendingDetailList;
import io.github.hamsteak.youtubetimelapse.trending.domain.TrendingSearcher;
import io.github.hamsteak.youtubetimelapse.trending.domain.dto.TrendingSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendingService {
    private final TrendingSearcher trendingSearcher;

    public List<DateTimeTrendingDetailList> searchTrending(TrendingSearchFilter filter) {
        return trendingSearcher.search(filter);
    }
}
