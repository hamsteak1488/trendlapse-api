package io.github.hamsteak.trendlapse.trendingsnapshot.application.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TrendingSnapshotVideoRow {
    @NonNull
    private final Long id;
    @NonNull
    private final String regionId;
    @NonNull
    private final LocalDateTime capturedAt;
    @NonNull
    private final Integer listIndex;
    @NonNull
    private final TrendingVideoView trendingVideoView;
}
