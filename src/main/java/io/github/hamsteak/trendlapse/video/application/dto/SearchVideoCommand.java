package io.github.hamsteak.trendlapse.video.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchVideoCommand {
    private final Long id;
    private final Long channelId;
    private final String youtubeId;
    private final String title;
}
