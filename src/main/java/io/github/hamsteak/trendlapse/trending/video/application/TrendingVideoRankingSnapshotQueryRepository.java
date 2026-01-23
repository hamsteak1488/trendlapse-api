package io.github.hamsteak.trendlapse.trending.video.application;

import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoRankingSnapshotView;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingVideoRankingSnapshotQueryRepository {
    List<TrendingVideoRankingSnapshotView> findViewByRegionAndCapturedAtIn(String regionId, List<LocalDateTime> captureTimes);
}
