package io.github.hamsteak.trendlapse.report.snapshot.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TrendingVideoRankingSnapshotReportView {
    private final long snapshotId;
    private final String summary;
}
