package io.github.hamsteak.trendlapse.trending.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendingSearchFilter {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
}
