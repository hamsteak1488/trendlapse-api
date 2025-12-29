package io.github.hamsteak.trendlapse.collector.application.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class FetchedChannel {
    private final String youtubeId;
    private final String title;
    private final String thumbnailUrl;
}
