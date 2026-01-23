package io.github.hamsteak.trendlapse.trending.video.application.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TrendingVideoRankingSnapshotView {
    @NonNull
    private final Long id;
    @NonNull
    private final String regionId;
    @NonNull
    private final LocalDateTime capturedAt;
    @NonNull
    private final List<TrendingVideoRankingSnapshotItemView> trendingVideoRankingSnapshotItemViews;
}
