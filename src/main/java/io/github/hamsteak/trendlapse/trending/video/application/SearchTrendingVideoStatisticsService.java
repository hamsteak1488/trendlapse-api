package io.github.hamsteak.trendlapse.trending.video.application;

import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoStatisticsView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchTrendingVideoStatisticsService {
    private final TrendingVideoQueryRepository trendingVideoQueryRepository;

    @Transactional(readOnly = true)
    public List<TrendingVideoStatisticsView> search(long videoId) {
        return trendingVideoQueryRepository.findStatisticsByVideoId(videoId);
    }
}
