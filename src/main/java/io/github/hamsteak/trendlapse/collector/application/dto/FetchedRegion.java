package io.github.hamsteak.trendlapse.collector.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FetchedRegion {
    private final String id;
    private final String name;
}
