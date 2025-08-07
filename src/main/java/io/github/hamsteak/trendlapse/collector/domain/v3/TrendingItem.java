package io.github.hamsteak.trendlapse.collector.domain.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class TrendingItem {
    private final long regionId;
    private final int rank;
    private final String videoYoutubeId;
}
