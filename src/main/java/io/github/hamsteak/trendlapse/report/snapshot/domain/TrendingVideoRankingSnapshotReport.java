package io.github.hamsteak.trendlapse.report.snapshot.domain;

import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TrendingVideoRankingSnapshotReport {
    @Id
    private Long snapshotId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private TrendingVideoRankingSnapshot snapshot;

    @Column
    @NotNull
    private String markdownContent;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;

    public TrendingVideoRankingSnapshotReport(TrendingVideoRankingSnapshot snapshot, String markdownContent) {
        this.snapshot = snapshot;
        this.markdownContent = markdownContent;
    }
}
