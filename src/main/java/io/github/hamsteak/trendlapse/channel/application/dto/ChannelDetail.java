package io.github.hamsteak.trendlapse.channel.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ChannelDetail {
    @NonNull
    private final Long id;
    @NonNull
    private final String youtubeId;
    @NonNull
    private final String title;
    @NonNull
    private final String thumbnailUrl;
}
