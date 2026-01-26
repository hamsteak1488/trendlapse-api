package io.github.hamsteak.trendlapse.video.application.dto;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class VideoSearchFilter {
    private final String youtubeId;
    private final String title;
    private final String channelTitle;
}
