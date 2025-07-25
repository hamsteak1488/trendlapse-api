package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.channel.domain.ChannelDetail;
import io.github.hamsteak.trendlapse.video.domain.VideoDetail;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
