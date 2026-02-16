package io.github.hamsteak.trendlapse.report.snapshot.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendingVideoRankingSnapshotReportSystemPromptRepository
        extends JpaRepository<TrendingVideoRankingSnapshotReportSystemPrompt, Long> {

}
