package io.github.hamsteak.trendlapse.trending.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class DateTimeTrendingDetailList {
    @NonNull
    private final LocalDateTime dateTime;

    @NonNull
    private final List<TrendingDetail> items;
}
