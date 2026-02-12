package io.github.hamsteak.trendlapse.report.snapshot.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendingVideoRankingSnapshotReportRepository extends JpaRepository<TrendingVideoRankingSnapshotReport, Long> {
}
