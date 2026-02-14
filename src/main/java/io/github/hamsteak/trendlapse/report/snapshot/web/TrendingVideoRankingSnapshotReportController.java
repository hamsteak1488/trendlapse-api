package io.github.hamsteak.trendlapse.report.snapshot.web;

import io.github.hamsteak.trendlapse.report.snapshot.application.CreateTrendingVideoRankingSnapshotReportService;
import io.github.hamsteak.trendlapse.report.snapshot.application.GetTrendingVideoRankingSnapshotReportService;
import io.github.hamsteak.trendlapse.report.snapshot.application.dto.TrendingVideoRankingSnapshotReportView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trendings/videos/ranking-snapshots")
@RequiredArgsConstructor
public class TrendingVideoRankingSnapshotReportController {
    private final GetTrendingVideoRankingSnapshotReportService getTrendingVideoRankingSnapshotReportService;
    private final CreateTrendingVideoRankingSnapshotReportService createTrendingVideoRankingSnapshotReportService;

    @GetMapping("/{snapshotId}/report")
    public ResponseEntity<?> getSnapshotReport(@PathVariable long snapshotId) {
        TrendingVideoRankingSnapshotReportView view = getTrendingVideoRankingSnapshotReportService.get(snapshotId);

        return ResponseEntity.ok(view);
    }

    @PostMapping("/{snapshotId}/report")
    public ResponseEntity<?> createSnapshotReport(@PathVariable long snapshotId) {
        createTrendingVideoRankingSnapshotReportService.create(snapshotId);

        return ResponseEntity.ok().build();
    }
}
