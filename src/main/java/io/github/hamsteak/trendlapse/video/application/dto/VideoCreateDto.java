package io.github.hamsteak.trendlapse.video.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VideoCreateDto {
    private final String youtubeId;
    private final String channelYoutubeId;
    private final String title;
    private final String thumbnailUrl;
}
