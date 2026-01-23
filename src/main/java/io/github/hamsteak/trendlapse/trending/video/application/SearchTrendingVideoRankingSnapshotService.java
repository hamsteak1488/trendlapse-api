package io.github.hamsteak.trendlapse.trending.video.application;

import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoRankingSnapshotSearchFilter;
import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoRankingSnapshotView;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchTrendingVideoRankingSnapshotService {
    private final TrendingVideoRankingSnapshotRepository trendingVideoRankingSnapshotRepository;
    private final TrendingVideoRankingSnapshotQueryRepository trendingVideoRankingSnapshotQueryRepository;

    private final CacheManager cacheManager;
    private final static String CACHE_NAME = "trendingVideoView";

    @Transactional(readOnly = true)
    @Timed("trending.search")
    public List<TrendingVideoRankingSnapshotView> search(TrendingVideoRankingSnapshotSearchFilter filter) {
        String regionId = filter.getRegionId();

        List<LocalDateTime> captureTimesInDb = trendingVideoRankingSnapshotRepository.findCaptureTimesByCapturedAtBetween(
                filter.getRegionId(),
                filter.getStartDateTime(),
                filter.getEndDateTime()
        );

        List<TrendingVideoRankingSnapshotView> resultTrendingVideoRankingSnapshotViews = new ArrayList<>();

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new IllegalStateException("Cache not found.");
        }

        List<LocalDateTime> missingCaptureTimes = new ArrayList<>();

        for (LocalDateTime captureTime : captureTimesInDb) {
            String cacheKey = getCacheKey(regionId, captureTime);
            TrendingVideoRankingSnapshotView trendingVideoRankingSnapshotView = cache.get(cacheKey, TrendingVideoRankingSnapshotView.class);

            if (trendingVideoRankingSnapshotView == null) {
                missingCaptureTimes.add(captureTime);
            } else {
                resultTrendingVideoRankingSnapshotViews.add(trendingVideoRankingSnapshotView);
            }
        }

        if (!missingCaptureTimes.isEmpty()) {
            List<TrendingVideoRankingSnapshotView> queriedTrendingVideoRankingSnapshotViews =
                    trendingVideoRankingSnapshotQueryRepository.findViewByRegionAndCapturedAtIn(regionId, missingCaptureTimes);

            queriedTrendingVideoRankingSnapshotViews.forEach(trendingVideoRankingSnapshotView ->
                    cache.put(
                            getCacheKey(regionId, trendingVideoRankingSnapshotView.getCapturedAt()),
                            trendingVideoRankingSnapshotView
                    )
            );

            resultTrendingVideoRankingSnapshotViews.addAll(queriedTrendingVideoRankingSnapshotViews);
        }

        resultTrendingVideoRankingSnapshotViews.sort(Comparator.comparing(TrendingVideoRankingSnapshotView::getCapturedAt));

        return resultTrendingVideoRankingSnapshotViews;
    }

    private static String getCacheKey(String regionCode, LocalDateTime dateTime) {
        return regionCode + ":" + dateTime;
    }
}
