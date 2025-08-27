package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.channel.domain.ChannelDetail;
import io.github.hamsteak.trendlapse.video.domain.VideoDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class TrendingDetail {
    @NonNull
    private final LocalDateTime dateTime;
    @NonNull
    private final Integer rank;
    @NonNull
    private final VideoDetail videoDetail;
    @NonNull
    private final ChannelDetail channelDetail;
}
