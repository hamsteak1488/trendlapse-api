package io.github.hamsteak.trendlapse.video.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchVideoRequest {
    private final String youtubeId;
    private final String title;
    private final String channelTitle;
}
