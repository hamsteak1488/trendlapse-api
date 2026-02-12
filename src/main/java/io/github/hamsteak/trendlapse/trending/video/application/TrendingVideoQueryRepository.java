package io.github.hamsteak.trendlapse.trending.video.application;

import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoRankingSnapshotView;
import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoStatisticsView;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotItem;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingVideoQueryRepository {
    List<TrendingVideoRankingSnapshotView> findRankingSnapshotViewByRegionAndCapturedAtIn(String regionId, List<LocalDateTime> captureTimes);

    List<TrendingVideoStatisticsView> findStatisticsByVideoId(long videoId);

    List<TrendingVideoRankingSnapshotItem> findRankingSnapshotItemByVideoIdIn(List<Long> videoIds);
}
