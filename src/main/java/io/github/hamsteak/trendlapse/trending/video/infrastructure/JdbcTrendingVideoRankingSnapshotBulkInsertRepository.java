package io.github.hamsteak.trendlapse.trending.video.infrastructure;

import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotBulkInsertRepository;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class JdbcTrendingVideoRankingSnapshotBulkInsertRepository implements TrendingVideoRankingSnapshotBulkInsertRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final int INSERT_BATCH_SIZE = 1000;

    /**
     * @param trendingVideoRankingSnapshots 모든 TrendingVideoRankingSnapshot 엔티티들은 capturedAt 값이 같아야 한다.
     */
    @Override
    public void bulkInsert(List<TrendingVideoRankingSnapshot> trendingVideoRankingSnapshots) {
        if (trendingVideoRankingSnapshots.isEmpty()) {
            return;
        }

        LocalDateTime captureTime = trendingVideoRankingSnapshots.get(0).getCapturedAt();
        validateAllCaptureTimeOfSnapshotsAreSame(trendingVideoRankingSnapshots, captureTime);

        insertTrendingVideoRankingSnapshots(trendingVideoRankingSnapshots);
        List<TrendingVideoRankingSnapshotRow> justInsertedTrendingVideoRankingSnapshotRows =
                findTrendingVideoRankingSnapshotRowsByCaptureTime(captureTime);

        findAndSetRowVideoIdsFromEntities(justInsertedTrendingVideoRankingSnapshotRows, trendingVideoRankingSnapshots);

        List<TrendingVideoRankingSnapshotItemRow> trendingVideoRankingSnapshotItemRowsToInsert =
                getTrendingVideoRankingSnapshotItemRowsToInsert(justInsertedTrendingVideoRankingSnapshotRows);
        insertTrendingVideoRankingSnapshotItems(trendingVideoRankingSnapshotItemRowsToInsert);
    }

    private void validateAllCaptureTimeOfSnapshotsAreSame(
            List<TrendingVideoRankingSnapshot> trendingVideoRankingSnapshots,
            LocalDateTime capturedAt
    ) {
        for (TrendingVideoRankingSnapshot trendingVideoRankingSnapshot : trendingVideoRankingSnapshots) {
            if (!trendingVideoRankingSnapshot.getCapturedAt().equals(capturedAt)) {
                throw new IllegalArgumentException("All items in the parameter list must have same capturedAt value.");
            }
        }
    }

    private void insertTrendingVideoRankingSnapshots(List<TrendingVideoRankingSnapshot> trendingVideoRankingSnapshots) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO trending_video_ranking_snapshot(region_id, captured_at) VALUES (?, ?)",
                trendingVideoRankingSnapshots,
                INSERT_BATCH_SIZE,
                (ps, snapshot) -> {
                    ps.setString(1, snapshot.getRegionId());
                    ps.setTimestamp(2, Timestamp.valueOf(snapshot.getCapturedAt()));
                }
        );
    }

    private List<TrendingVideoRankingSnapshotRow> findTrendingVideoRankingSnapshotRowsByCaptureTime(LocalDateTime capturedAt) {
        String sql = """
                SELECT id, region_id, captured_at
                FROM trending_video_ranking_snapshot
                WHERE captured_at >= ? AND captured_at < ?
                """;
        LocalDateTime start = capturedAt.truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = start.plusSeconds(1);
        //

        return jdbcTemplate.query(
                sql,
                trendingVideoRankingSnapshotRowMapper(),
                Timestamp.valueOf(start),
                Timestamp.valueOf(end)
        );
    }

    private void findAndSetRowVideoIdsFromEntities(
            List<TrendingVideoRankingSnapshotRow> trendingVideoRankingSnapshotRows,
            List<TrendingVideoRankingSnapshot> trendingVideoRankingSnapshots
    ) {
        trendingVideoRankingSnapshotRows.forEach(trendingVideoRankingSnapshotRow ->
                trendingVideoRankingSnapshotRow.setVideoIds(
                        trendingVideoRankingSnapshots.stream()
                                .filter(snapshot ->
                                        snapshot.getRegionId().equals(trendingVideoRankingSnapshotRow.getRegionId())
                                )
                                .findAny()
                                .orElseThrow(() -> new IllegalStateException("Cannot find "))
                                .getTrendingVideoRankingSnapshotItems()
                                .stream()
                                .map(TrendingVideoRankingSnapshotItem::getVideoId)
                                .toList()
                )
        );
    }

    private List<TrendingVideoRankingSnapshotItemRow> getTrendingVideoRankingSnapshotItemRowsToInsert(
            List<TrendingVideoRankingSnapshotRow> justInsertedTrendingVideoRankingSnapshotRows
    ) {
        return justInsertedTrendingVideoRankingSnapshotRows.stream()
                .flatMap(trendingVideoRankingSnapshotRow ->
                        IntStream.range(0, trendingVideoRankingSnapshotRow.getVideoIds().size())
                                .mapToObj(index -> new TrendingVideoRankingSnapshotItemRow(
                                        trendingVideoRankingSnapshotRow.getId(),
                                        trendingVideoRankingSnapshotRow.getVideoIds().get(index),
                                        index
                                ))
                )
                .toList();
    }

    private void insertTrendingVideoRankingSnapshotItems(
            List<TrendingVideoRankingSnapshotItemRow> trendingVideoRankingSnapshotItemRows
    ) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO trending_video_ranking_snapshot_item(snapshot_id, video_id, list_index) VALUES (?, ?, ?)",
                trendingVideoRankingSnapshotItemRows,
                INSERT_BATCH_SIZE,
                (ps, trendingVideoRankingSnapshotItemRow) -> {
                    ps.setLong(1, trendingVideoRankingSnapshotItemRow.getSnapshotId());
                    ps.setLong(2, trendingVideoRankingSnapshotItemRow.getVideoId());
                    ps.setInt(3, trendingVideoRankingSnapshotItemRow.getIndex());
                }
        );
    }

    private RowMapper<TrendingVideoRankingSnapshotRow> trendingVideoRankingSnapshotRowMapper() {
        return (rs, rowNum) -> {
            long id = rs.getLong("id");
            String regionId = rs.getString("region_id");
            LocalDateTime capturedAt = rs.getTimestamp("captured_at").toLocalDateTime();

            return new TrendingVideoRankingSnapshotRow(id, regionId, capturedAt);
        };
    }

    @Getter
    @RequiredArgsConstructor
    private static class TrendingVideoRankingSnapshotRow {
        private final long id;
        private final String regionId;
        private final LocalDateTime capturedAt;
        @Setter
        private List<Long> videoIds;
    }

    @Getter
    @RequiredArgsConstructor
    private static class TrendingVideoRankingSnapshotItemRow {
        private final long snapshotId;
        private final long videoId;
        private final int index;
    }
}
