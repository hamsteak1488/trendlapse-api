package io.github.hamsteak.trendlapse.trending.video.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.github.hamsteak.trendlapse.channel.application.dto.ChannelView;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@QueryProjection
@Getter
@Builder
@RequiredArgsConstructor
public class TrendingVideoRankingSnapshotItemView {
    @NonNull
    private final Long snapshotId;
    @NonNull
    private final Integer listIndex;
    @NonNull
    private final VideoView videoView;
    @NonNull
    private final ChannelView channelView;
}
