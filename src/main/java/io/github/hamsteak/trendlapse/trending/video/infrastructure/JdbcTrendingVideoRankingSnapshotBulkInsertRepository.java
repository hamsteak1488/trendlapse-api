package io.github.hamsteak.trendlapse.trending.video.infrastructure;

import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotBulkInsertRepository;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
     * @param snapshots 모든 TrendingVideoRankingSnapshot 엔티티들은 capturedAt 값이 같아야 한다.
     */
    @Override
    public void bulkInsert(List<TrendingVideoRankingSnapshot> snapshots) {
        if (snapshots.isEmpty()) {
            return;
        }

        LocalDateTime captureTime = snapshots.get(0).getCapturedAt();
        validateAllCaptureTimeOfSnapshotsAreSame(snapshots, captureTime);

        insertTrendingVideoRankingSnapshots(snapshots);
        List<ItemsLackedTrendingVideoRankingSnapshotRow> justInsertedItemLackedSnapshotRows =
                findTrendingVideoRankingSnapshotRowsByCaptureTime(captureTime);

        List<TrendingVideoRankingSnapshotRow> itemSetupedSnapshotRows =
                getItemSetupedSnapshotRows(justInsertedItemLackedSnapshotRows, snapshots);

        List<TrendingVideoRankingSnapshotItemRow> snapshotItemRowsToInsert =
                getTrendingVideoRankingSnapshotItemRowsToInsert(itemSetupedSnapshotRows);

        insertTrendingVideoRankingSnapshotItems(snapshotItemRowsToInsert);
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

    private List<ItemsLackedTrendingVideoRankingSnapshotRow> findTrendingVideoRankingSnapshotRowsByCaptureTime(LocalDateTime capturedAt) {
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

    private List<TrendingVideoRankingSnapshotRow> getItemSetupedSnapshotRows(
            List<ItemsLackedTrendingVideoRankingSnapshotRow> itemsLackedSnapshotRows,
            List<TrendingVideoRankingSnapshot> snapshots
    ) {
        return itemsLackedSnapshotRows.stream()
                .map(snapshotRow -> {
                            List<TrendingVideoRankingSnapshotItem> items = snapshots.stream()
                                    .filter(snapshot ->
                                            snapshot.getRegionId().equals(snapshotRow.getRegionId()))
                                    .findAny()
                                    .orElseThrow(() -> new IllegalStateException("Cannot find snapshot having same region id."))
                                    .getItems();

                            List<Long> videoIds = items.stream()
                                    .map(TrendingVideoRankingSnapshotItem::getVideoId)
                                    .toList();
                            List<Long> viewCounts = items.stream()
                                    .map(TrendingVideoRankingSnapshotItem::getViewCount)
                                    .toList();
                            List<Long> likeCounts = items.stream()
                                    .map(TrendingVideoRankingSnapshotItem::getLikeCount)
                                    .toList();
                            List<Long> commentCounts = items.stream()
                                    .map(TrendingVideoRankingSnapshotItem::getCommentCount)
                                    .toList();

                            return new TrendingVideoRankingSnapshotRow(
                                    snapshotRow.id,
                                    snapshotRow.regionId,
                                    snapshotRow.capturedAt,
                                    videoIds,
                                    viewCounts,
                                    likeCounts,
                                    commentCounts
                            );
                        }
                )
                .toList();
    }

    private List<TrendingVideoRankingSnapshotItemRow> getTrendingVideoRankingSnapshotItemRowsToInsert(
            List<TrendingVideoRankingSnapshotRow> justInsertedSnapshotRows
    ) {
        return justInsertedSnapshotRows.stream()
                .flatMap(trendingVideoRankingSnapshotRow ->
                        IntStream.range(0, trendingVideoRankingSnapshotRow.getVideoIds().size())
                                .mapToObj(index -> new TrendingVideoRankingSnapshotItemRow(
                                        trendingVideoRankingSnapshotRow.getId(),
                                        trendingVideoRankingSnapshotRow.getVideoIds().get(index),
                                        index,
                                        trendingVideoRankingSnapshotRow.getViewCount().get(index),
                                        trendingVideoRankingSnapshotRow.getLikeCount().get(index),
                                        trendingVideoRankingSnapshotRow.getCommentCount().get(index)
                                ))
                )
                .toList();
    }

    private void insertTrendingVideoRankingSnapshotItems(
            List<TrendingVideoRankingSnapshotItemRow> trendingVideoRankingSnapshotItemRows
    ) {
        jdbcTemplate.batchUpdate("""
                        INSERT INTO trending_video_ranking_snapshot_item
                        (snapshot_id,video_id, list_index, view_count, like_count, comment_count)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                trendingVideoRankingSnapshotItemRows,
                INSERT_BATCH_SIZE,
                (ps, trendingVideoRankingSnapshotItemRow) -> {
                    ps.setLong(1, trendingVideoRankingSnapshotItemRow.getSnapshotId());
                    ps.setLong(2, trendingVideoRankingSnapshotItemRow.getVideoId());
                    ps.setInt(3, trendingVideoRankingSnapshotItemRow.getIndex());
                    ps.setLong(4, trendingVideoRankingSnapshotItemRow.getViewCount());
                    ps.setLong(5, trendingVideoRankingSnapshotItemRow.getLikeCount());
                    ps.setLong(6, trendingVideoRankingSnapshotItemRow.getCommentCount());
                }
        );
    }

    private RowMapper<ItemsLackedTrendingVideoRankingSnapshotRow> trendingVideoRankingSnapshotRowMapper() {
        return (rs, rowNum) -> {
            long id = rs.getLong("id");
            String regionId = rs.getString("region_id");
            LocalDateTime capturedAt = rs.getTimestamp("captured_at").toLocalDateTime();

            return new ItemsLackedTrendingVideoRankingSnapshotRow(id, regionId, capturedAt);
        };
    }

    @Getter
    @RequiredArgsConstructor
    private static class ItemsLackedTrendingVideoRankingSnapshotRow {
        private final long id;
        private final String regionId;
        private final LocalDateTime capturedAt;
    }

    @Getter
    @RequiredArgsConstructor
    private static class TrendingVideoRankingSnapshotRow {
        private final long id;
        private final String regionId;
        private final LocalDateTime capturedAt;
        private final List<Long> videoIds;
        private final List<Long> viewCount;
        private final List<Long> likeCount;
        private final List<Long> commentCount;
    }

    @Getter
    @RequiredArgsConstructor
    private static class TrendingVideoRankingSnapshotItemRow {
        private final long snapshotId;
        private final long videoId;
        private final int index;
        private final long viewCount;
        private final long likeCount;
        private final long commentCount;
    }
}
