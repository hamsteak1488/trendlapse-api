package io.github.hamsteak.trendlapse.trendingsnapshot.domain;

import java.util.List;

public interface TrendingSnapshotBulkInsertRepository {
    void bulkInsert(List<TrendingSnapshot> trendingSnapshots);
}
