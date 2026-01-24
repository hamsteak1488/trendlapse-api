package io.github.hamsteak.trendlapse.trending.video.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@QueryProjection
@Getter
@Builder
@RequiredArgsConstructor
public class TrendingVideoStatisticsView {
    @NonNull
    private final Long snapshotId;
    @NonNull
    private final Integer listIndex;
    @NonNull
    private final Long viewCount;
    @NonNull
    private final Long likeCount;
    @NonNull
    private final Long commentCount;
}
