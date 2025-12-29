package io.github.hamsteak.trendlapse.video.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@QueryProjection
@Getter
@Builder
@RequiredArgsConstructor
public class VideoView {
    @NonNull
    private final Long id;
    @NonNull
    private final Long channelId;
    @NonNull
    private final String youtubeId;
    @NonNull
    private final String title;
    @NonNull
    private final String thumbnailUrl;
}
