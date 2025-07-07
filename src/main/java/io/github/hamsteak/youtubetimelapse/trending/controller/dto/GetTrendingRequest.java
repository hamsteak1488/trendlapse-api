package io.github.hamsteak.youtubetimelapse.trending.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GetTrendingRequest {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
}
