package io.github.hamsteak.trendlapse.trendingsnapshot.application;

import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingSnapshotView;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingSnapshotQueryRepository {
    List<TrendingSnapshotView> findViewByRegionAndCapturedAtIn(String regionId, List<LocalDateTime> captureTimes);
}
