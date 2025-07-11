package io.github.hamsteak.youtubetimelapse.channel.domain;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelDetail {
    @NonNull
    private final Long id;
    @NonNull
    private final String youtubeId;
    @NonNull
    private final String title;
    @NonNull
    private final String thumbnailUrl;
}
