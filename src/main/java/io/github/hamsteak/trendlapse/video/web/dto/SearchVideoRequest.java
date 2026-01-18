package io.github.hamsteak.trendlapse.video.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchVideoRequest {
    private final Long id;
    private final Long channelId;
    private final String youtubeId;
    private final String title;
}
