package io.github.hamsteak.trendlapse.trendingsnapshot.application;

import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingSnapshotView;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotRepository;
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
public class SearchTrendingSnapshotService {
    private final TrendingSnapshotRepository trendingSnapshotRepository;
    private final TrendingSnapshotQueryRepository trendingSnapshotQueryRepository;

    private final CacheManager cacheManager;
    private final static String CACHE_NAME = "trendingVideoView";

    @Transactional(readOnly = true)
    @Timed("trending.search")
    public List<TrendingSnapshotView> search(TrendingSearchFilter filter) {
        String regionId = filter.getRegionId();

        List<LocalDateTime> captureTimesInDb = trendingSnapshotRepository.findCaptureTimesByCapturedAtBetween(
                filter.getRegionId(),
                filter.getStartDateTime(),
                filter.getEndDateTime()
        );

        List<TrendingSnapshotView> resultTrendingSnapshotViews = new ArrayList<>();

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new IllegalStateException("Cache not found.");
        }

        List<LocalDateTime> missingCaptureTimes = new ArrayList<>();

        for (LocalDateTime captureTime : captureTimesInDb) {
            String cacheKey = getCacheKey(regionId, captureTime);
            TrendingSnapshotView trendingSnapshotView = cache.get(cacheKey, TrendingSnapshotView.class);

            if (trendingSnapshotView == null) {
                missingCaptureTimes.add(captureTime);
            } else {
                resultTrendingSnapshotViews.add(trendingSnapshotView);
            }
        }

        if (!missingCaptureTimes.isEmpty()) {
            List<TrendingSnapshotView> queriedTrendingSnapshotViews =
                    trendingSnapshotQueryRepository.findViewByRegionAndCapturedAtIn(regionId, missingCaptureTimes);

            queriedTrendingSnapshotViews.forEach(trendingSnapshotView ->
                    cache.put(
                            getCacheKey(regionId, trendingSnapshotView.getCapturedAt()),
                            trendingSnapshotView
                    )
            );

            resultTrendingSnapshotViews.addAll(queriedTrendingSnapshotViews);
        }

        resultTrendingSnapshotViews.sort(Comparator.comparing(TrendingSnapshotView::getCapturedAt));

        return resultTrendingSnapshotViews;
    }

    private static String getCacheKey(String regionCode, LocalDateTime dateTime) {
        return regionCode + ":" + dateTime;
    }
}
