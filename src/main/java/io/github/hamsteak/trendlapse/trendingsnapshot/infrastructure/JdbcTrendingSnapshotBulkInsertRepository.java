package io.github.hamsteak.trendlapse.trendingsnapshot.infrastructure;

import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshot;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotBulkInsertRepository;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotVideo;
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
public class JdbcTrendingSnapshotBulkInsertRepository implements TrendingSnapshotBulkInsertRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final int INSERT_BATCH_SIZE = 1000;

    /**
     * @param trendingSnapshots 모든 TrendingSnapshot 엔티티들은 capturedAt 값이 같아야 한다.
     */
    @Override
    public void bulkInsert(List<TrendingSnapshot> trendingSnapshots) {
        if (trendingSnapshots.isEmpty()) {
            return;
        }

        LocalDateTime captureTime = trendingSnapshots.get(0).getCapturedAt();
        validateAllCaptureTimeOfSnapshotsAreSame(trendingSnapshots, captureTime);
        insertTrendingSnapshots(trendingSnapshots);
        List<TrendingSnapshotRow> justInsertedTrendingSnapshotRows = findTrendingSnapshotRowsByCaptureTime(captureTime);
        setRowVideoIds(justInsertedTrendingSnapshotRows, trendingSnapshots);
        List<TrendingSnapshotVideoRow> trendingSnapshotVideoRowsToInsert = getTrendingSnapshotVideoRowsToInsert(justInsertedTrendingSnapshotRows);
        insertTrendingSnapshotVideos(trendingSnapshotVideoRowsToInsert);
    }

    private void validateAllCaptureTimeOfSnapshotsAreSame(List<TrendingSnapshot> trendingSnapshots, LocalDateTime capturedAt) {
        for (TrendingSnapshot trendingSnapshot : trendingSnapshots) {
            if (!trendingSnapshot.getCapturedAt().equals(capturedAt)) {
                throw new IllegalArgumentException("All items in the parameter list must have same capturedAt value.");
            }
        }
    }

    private void insertTrendingSnapshots(List<TrendingSnapshot> trendingSnapshots) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO trending_snapshot(region_id, captured_at) VALUES (?, ?)",
                trendingSnapshots,
                INSERT_BATCH_SIZE,
                (ps, trendingSnapshot) -> {
                    ps.setString(1, trendingSnapshot.getRegionId());
                    ps.setTimestamp(2, Timestamp.valueOf(trendingSnapshot.getCapturedAt()));
                }
        );
    }

    private List<TrendingSnapshotRow> findTrendingSnapshotRowsByCaptureTime(LocalDateTime capturedAt) {
        String sql = """
                SELECT id, region_id, captured_at
                FROM trending_snapshot
                WHERE captured_at >= ? AND captured_at < ?
                """;
        LocalDateTime start = capturedAt.truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = start.plusSeconds(1);
        //

        return jdbcTemplate.query(
                sql,
                trendingSnapshotRowMapper(),
                Timestamp.valueOf(start),
                Timestamp.valueOf(end)
        );
    }

    private void setRowVideoIds(List<TrendingSnapshotRow> trendingSnapshotRows, List<TrendingSnapshot> trendingSnapshots) {
        trendingSnapshotRows.forEach(trendingSnapshotRow ->
                trendingSnapshotRow.setTrendingVideoIds(
                        trendingSnapshots.stream()
                                .filter(trendingSnapshot ->
                                        trendingSnapshot.getRegionId().equals(trendingSnapshotRow.getRegionId())
                                )
                                .findAny()
                                .orElseThrow(() -> new IllegalStateException("Cannot find "))
                                .getTrendingSnapshotVideos()
                                .stream()
                                .map(TrendingSnapshotVideo::getTrendingVideoId)
                                .toList()
                )
        );
    }

    private List<TrendingSnapshotVideoRow> getTrendingSnapshotVideoRowsToInsert(List<TrendingSnapshotRow> justInsertedTrendingSnapshotRows) {
        return justInsertedTrendingSnapshotRows.stream()
                .flatMap(trendingSnapshotRow ->
                        IntStream.range(0, trendingSnapshotRow.getTrendingVideoIds().size())
                                .mapToObj(index -> new TrendingSnapshotVideoRow(
                                        trendingSnapshotRow.getId(),
                                        trendingSnapshotRow.getTrendingVideoIds().get(index),
                                        index
                                ))
                )
                .toList();
    }

    private void insertTrendingSnapshotVideos(List<TrendingSnapshotVideoRow> trendingSnapshotVideoRows) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO trending_snapshot_video(trending_snapshot_id, trending_video_id, list_idx) VALUES (?, ?, ?)",
                trendingSnapshotVideoRows,
                INSERT_BATCH_SIZE,
                (ps, trendingSnapshotVideoRow) -> {
                    ps.setLong(1, trendingSnapshotVideoRow.getTrendingSnapshotId());
                    ps.setLong(2, trendingSnapshotVideoRow.getTrendingVideoId());
                    ps.setInt(3, trendingSnapshotVideoRow.getIndex());
                }
        );
    }

    private RowMapper<TrendingSnapshotRow> trendingSnapshotRowMapper() {
        return (rs, rowNum) -> {
            long id = rs.getLong("id");
            String regionId = rs.getString("region_id");
            LocalDateTime capturedAt = rs.getTimestamp("captured_at").toLocalDateTime();

            return new TrendingSnapshotRow(id, regionId, capturedAt);
        };
    }

    @Getter
    @RequiredArgsConstructor
    private static class TrendingSnapshotRow {
        private final long id;
        private final String regionId;
        private final LocalDateTime capturedAt;
        @Setter
        private List<Long> trendingVideoIds;
    }

    @Getter
    @RequiredArgsConstructor
    private static class TrendingSnapshotVideoRow {
        private final long trendingSnapshotId;
        private final long trendingVideoId;
        private final int index;
    }
}
