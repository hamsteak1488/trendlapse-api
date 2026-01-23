package io.github.hamsteak.trendlapse.trending.video.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendingVideoRankingSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String regionId;

    @Column
    @NotNull
    private LocalDateTime capturedAt;

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrendingVideoRankingSnapshotItem> trendingVideoRankingSnapshotItems = new ArrayList<>();

    public static TrendingVideoRankingSnapshot createTrendingVideoRankingSnapshot(
            String regionId,
            LocalDateTime capturedAt,
            List<Long> trendingVideoIds
    ) {
        TrendingVideoRankingSnapshot trendingVideoRankingSnapshot = new TrendingVideoRankingSnapshot();
        trendingVideoRankingSnapshot.regionId = regionId;
        trendingVideoRankingSnapshot.capturedAt = capturedAt;
        trendingVideoRankingSnapshot.trendingVideoRankingSnapshotItems.addAll(
                trendingVideoIds.stream()
                        .map(videoId ->
                                TrendingVideoRankingSnapshotItem.createTrendingVideoRankingSnapshotItem(
                                        trendingVideoRankingSnapshot,
                                        videoId,
                                        1)
                        )
                        .toList()
        );

        return trendingVideoRankingSnapshot;
    }
}
