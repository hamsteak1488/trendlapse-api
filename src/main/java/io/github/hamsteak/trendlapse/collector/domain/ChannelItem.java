package io.github.hamsteak.trendlapse.collector.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ChannelItem {
    private final String youtubeId;
    private final String title;
    private final String thumbnailUrl;
}
