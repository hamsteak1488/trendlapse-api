package io.github.hamsteak.trendlapse.trending.video.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendingVideoRankingSnapshotSearchFilter {
    @NonNull
    private final String regionId;
    @NonNull
    private final LocalDateTime startDateTime;
    @NonNull
    private final LocalDateTime endDateTime;
}
