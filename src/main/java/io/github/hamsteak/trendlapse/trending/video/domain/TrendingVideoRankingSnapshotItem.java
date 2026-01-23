package io.github.hamsteak.trendlapse.trending.video.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendingVideoRankingSnapshotItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private TrendingVideoRankingSnapshot snapshot;

    @Column
    @NotNull
    private Long videoId;

    @Column
    @NotNull
    private Integer listIndex;

    public static TrendingVideoRankingSnapshotItem createTrendingVideoRankingSnapshotItem(
            TrendingVideoRankingSnapshot trendingVideoRankingSnapshot,
            long trendingVideoId,
            int listIndex
    ) {
        return new TrendingVideoRankingSnapshotItem(null, trendingVideoRankingSnapshot, trendingVideoId, listIndex);
    }
}
