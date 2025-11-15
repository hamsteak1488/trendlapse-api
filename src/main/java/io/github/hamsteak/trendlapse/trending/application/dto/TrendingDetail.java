package io.github.hamsteak.trendlapse.trending.application.dto;

import io.github.hamsteak.trendlapse.channel.application.dto.ChannelDetail;
import io.github.hamsteak.trendlapse.video.application.dto.VideoDetail;
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
