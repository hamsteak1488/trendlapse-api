package io.github.hamsteak.youtubetimelapse.trending.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendingDetail {
    @NonNull
    private final LocalDateTime dateTime;

    @NonNull
    private final String youtubeId;

    @NonNull
    private final Integer rank;
}
