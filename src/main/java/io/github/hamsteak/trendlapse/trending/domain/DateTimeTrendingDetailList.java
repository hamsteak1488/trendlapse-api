package io.github.hamsteak.trendlapse.trending.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeTrendingDetailList {
    @NonNull
    private final LocalDateTime dateTime;

    @NonNull
    List<TrendingDetail> items;
}
