package io.github.hamsteak.trendlapse.trending.video.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Long viewCount;

    @Column
    @NotNull
    private Long likeCount;

    @Column
    @NotNull
    private Long commentCount;

    @Column
    @NotNull
    private Integer listIndex;

    @Builder
    private TrendingVideoRankingSnapshotItem(Long id, TrendingVideoRankingSnapshot snapshot, Long videoId, Long viewCount, Long likeCount, Long commentCount, Integer listIndex) {
        this.id = id;
        this.snapshot = snapshot;
        this.videoId = videoId;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.listIndex = listIndex;
    }
}
