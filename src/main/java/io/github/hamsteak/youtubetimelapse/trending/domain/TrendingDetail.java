package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.channel.domain.ChannelDetail;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoDetail;
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
