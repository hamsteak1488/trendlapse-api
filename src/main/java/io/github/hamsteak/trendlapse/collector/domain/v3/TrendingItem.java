package io.github.hamsteak.trendlapse.collector.domain.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@ToString
public class TrendingItem {
    private final LocalDateTime dateTime;
    private final String regionCode;
    private final int rank;
    private final String videoYoutubeId;
}
