package io.github.hamsteak.trendlapse.collector.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RegionFetchedTrendingVideos {
    private final String regionId;
    private final List<FetchedVideo> fetchedTrendingVideos;
}
