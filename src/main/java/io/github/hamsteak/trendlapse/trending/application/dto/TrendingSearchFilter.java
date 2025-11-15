package io.github.hamsteak.trendlapse.trending.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendingSearchFilter {
    @NonNull
    private final String regionCode;

    @NonNull
    private final LocalDateTime startDateTime;

    @NonNull
    private final LocalDateTime endDateTime;
}
