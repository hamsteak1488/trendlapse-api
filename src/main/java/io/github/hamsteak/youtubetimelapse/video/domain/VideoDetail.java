package io.github.hamsteak.youtubetimelapse.video.domain;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoDetail {
    @NonNull
    private final Long id;
    @NonNull
    private final Long channelId;
    @NonNull
    private final String youtubeId;
    @NonNull
    private final String title;
    @NonNull
    private final String thumbnailUrl;
}
