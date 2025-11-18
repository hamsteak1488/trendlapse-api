package io.github.hamsteak.trendlapse.collector.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegionItem {
    private final String regionCode;
    private final String name;
    private final String isoCode;
}
