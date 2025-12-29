package io.github.hamsteak.trendlapse.trendingsnapshot.application.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TrendingSnapshotView {
    @NonNull
    private final Long id;
    @NonNull
    private final String regionId;
    @NonNull
    private final LocalDateTime capturedAt;
    @NonNull
    private final List<TrendingVideoView> trendingVideoViews;
}
