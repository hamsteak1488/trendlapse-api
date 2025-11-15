package io.github.hamsteak.trendlapse.trending.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class GetTrendingRequest {
    @NotEmpty
    private final String regionCode;

    @NotNull
    private final ZonedDateTime startDateTime;

    @NotNull
    private final ZonedDateTime endDateTime;
}
