package io.github.hamsteak.trendlapse.trendingsnapshot.domain;

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
public class TrendingSnapshotVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private TrendingSnapshot trendingSnapshot;

    @Column
    @NotNull
    private Long trendingVideoId;

    @Column(name = "list_idx")
    @NotNull
    private Integer listIndex;

    public static TrendingSnapshotVideo createTrendingSnapshotVideo(TrendingSnapshot trendingSnapshot, long trendingVideoId, int listIndex) {
        return new TrendingSnapshotVideo(null, trendingSnapshot, trendingVideoId, listIndex);
    }
}
