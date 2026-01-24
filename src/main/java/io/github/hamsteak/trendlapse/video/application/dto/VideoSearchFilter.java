package io.github.hamsteak.trendlapse.video.application.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class VideoSearchFilter {
    private final Long channelId;
    private final String youtubeId;
    private final String title;
}
