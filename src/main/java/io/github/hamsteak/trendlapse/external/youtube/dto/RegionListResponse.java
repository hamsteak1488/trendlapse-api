package io.github.hamsteak.trendlapse.external.youtube.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RegionListResponse {
    private final List<RegionResponse> items;
}
