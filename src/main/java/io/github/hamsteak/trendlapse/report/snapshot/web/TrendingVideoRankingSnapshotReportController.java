package io.github.hamsteak.trendlapse.report.snapshot.web;

import io.github.hamsteak.trendlapse.report.snapshot.application.GetTrendingVideoRankingSnapshotReportService;
import io.github.hamsteak.trendlapse.report.snapshot.application.dto.TrendingVideoRankingSnapshotReportView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trendings/videos/ranking-snapshots")
@RequiredArgsConstructor
public class TrendingVideoRankingSnapshotReportController {
    private final GetTrendingVideoRankingSnapshotReportService getTrendingVideoRankingSnapshotReportService;

    @GetMapping("/{snapshotId}/report")
    public ResponseEntity<?> getSnapshotReport(@PathVariable long snapshotId) {
        TrendingVideoRankingSnapshotReportView view = getTrendingVideoRankingSnapshotReportService.get(snapshotId);

        return ResponseEntity.ok(view);
    }
}
