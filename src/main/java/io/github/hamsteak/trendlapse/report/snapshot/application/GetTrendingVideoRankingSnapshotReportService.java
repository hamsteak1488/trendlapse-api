package io.github.hamsteak.trendlapse.report.snapshot.application;

import io.github.hamsteak.trendlapse.report.snapshot.application.dto.TrendingVideoRankingSnapshotReportView;
import io.github.hamsteak.trendlapse.report.snapshot.domain.SnapshotReportNotFoundException;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReport;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTrendingVideoRankingSnapshotReportService {
    private final TrendingVideoRankingSnapshotReportRepository snapshotReportRepository;

    public TrendingVideoRankingSnapshotReportView get(long snapshotId) {
        TrendingVideoRankingSnapshotReport snapshotReport = snapshotReportRepository.findById(snapshotId)
                .orElseThrow(() -> new SnapshotReportNotFoundException("Cannot find snapshot report."));

        return new TrendingVideoRankingSnapshotReportView(snapshotReport.getSnapshotId(), snapshotReport.getMarkdownContent());
    }
}
