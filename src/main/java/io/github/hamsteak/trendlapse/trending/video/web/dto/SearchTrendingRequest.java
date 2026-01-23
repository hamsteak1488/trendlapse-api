package io.github.hamsteak.trendlapse.trending.video.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class SearchTrendingRequest {
    @NotEmpty
    private final String regionId;

    @NotNull
    private final ZonedDateTime startDateTime;

    @NotNull
    private final ZonedDateTime endDateTime;
}
