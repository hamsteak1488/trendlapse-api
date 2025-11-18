package io.github.hamsteak.trendlapse.channel.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChannelCreateDto {
    private final String youtubeId;
    private final String title;
    private final String thumbnailUrl;
}
