package io.github.hamsteak.trendlapse.trendingsnapshot.domain;

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
public class TrendingSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String regionId;

    @Column
    @NotNull
    private LocalDateTime capturedAt;

    @OneToMany(mappedBy = "trendingSnapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrendingSnapshotVideo> trendingSnapshotVideos = new ArrayList<>();

    public static TrendingSnapshot createTrendingSnapshot(String regionId, LocalDateTime capturedAt, List<Long> trendingSnapshotVideoIds) {
        TrendingSnapshot trendingSnapshot = new TrendingSnapshot();
        trendingSnapshot.regionId = regionId;
        trendingSnapshot.capturedAt = capturedAt;
        trendingSnapshot.trendingSnapshotVideos.addAll(
                trendingSnapshotVideoIds.stream()
                        .map(videoId -> TrendingSnapshotVideo.createTrendingSnapshotVideo(trendingSnapshot, videoId, 1))
                        .toList()
        );

        return trendingSnapshot;
    }
}
