package io.github.hamsteak.trendlapse.trending.video.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendingVideoRankingSnapshotItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id", nullable = false)
    @JsonIgnore
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
}
