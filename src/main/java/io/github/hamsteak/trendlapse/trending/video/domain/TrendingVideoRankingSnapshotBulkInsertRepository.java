package io.github.hamsteak.trendlapse.trending.video.domain;

import java.util.List;

public interface TrendingVideoRankingSnapshotBulkInsertRepository {
    void bulkInsert(List<TrendingVideoRankingSnapshot> trendingVideoRankingSnapshots);
}
