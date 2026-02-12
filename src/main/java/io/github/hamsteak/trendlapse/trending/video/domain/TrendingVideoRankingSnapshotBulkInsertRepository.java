package io.github.hamsteak.trendlapse.trending.video.domain;

import java.util.List;

public interface TrendingVideoRankingSnapshotBulkInsertRepository {
    List<Long> bulkInsert(List<TrendingVideoRankingSnapshot> trendingVideoRankingSnapshots);
}
