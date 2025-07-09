package io.github.hamsteak.youtubetimelapse.trending.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class GetTrendingRequest {
    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
}
