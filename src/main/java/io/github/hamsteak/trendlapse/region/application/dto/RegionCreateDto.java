package io.github.hamsteak.trendlapse.region.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegionCreateDto {
    private final String regionCode;
    private final String name;
    private final String isoCode;
}
