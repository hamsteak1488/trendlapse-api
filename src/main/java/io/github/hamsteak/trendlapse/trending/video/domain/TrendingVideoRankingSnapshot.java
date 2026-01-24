package io.github.hamsteak.trendlapse.trending.video.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
    private List<TrendingVideoRankingSnapshotItem> items = new ArrayList<>();

    public static TrendingVideoRankingSnapshot createTrendingVideoRankingSnapshot(
            String regionId,
            LocalDateTime capturedAt,
            List<Long> trendingVideoIds,
            List<Long> viewCounts,
            List<Long> likeCounts,
            List<Long> commentCounts
    ) {
        TrendingVideoRankingSnapshot snapshot = new TrendingVideoRankingSnapshot();
        snapshot.regionId = regionId;
        snapshot.capturedAt = capturedAt;
        snapshot.items.addAll(
                IntStream.range(0, trendingVideoIds.size())
                        .mapToObj(index -> TrendingVideoRankingSnapshotItem.builder()
                                .snapshot(snapshot)
                                .videoId(trendingVideoIds.get(index))
                                .listIndex(index)
                                .viewCount(viewCounts.get(index))
                                .likeCount(likeCounts.get(index))
                                .commentCount(commentCounts.get(index))
                                .build()
                        )
                        .toList()
        );

        return snapshot;
    }
}
