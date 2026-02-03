package io.github.hamsteak.trendlapse.collector.application.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class FetchedRegion {
    private final String id;
    private final String name;
}
