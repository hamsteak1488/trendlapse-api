package io.github.hamsteak.trendlapse.trending.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TrendingCreateDto {
    private final LocalDateTime dateTime;
    private final String videoYoutubeId;
    private final int rank;
    private final String regionCode;
}
