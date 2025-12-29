package io.github.hamsteak.trendlapse.trendingsnapshot.application.dto;

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
public class TrendingVideoView {
    @NonNull
    private final Long trendingSnapshotId;
    @NonNull
    private final Integer listIndex;
    @NonNull
    private final VideoView videoView;
    @NonNull
    private final ChannelView channelView;
}
