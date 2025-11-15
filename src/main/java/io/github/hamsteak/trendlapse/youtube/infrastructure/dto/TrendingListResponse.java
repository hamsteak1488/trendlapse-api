package io.github.hamsteak.trendlapse.youtube.infrastructure.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * nextPageToken: Trending page offset 역할.
 */
@Getter
@RequiredArgsConstructor
public class TrendingListResponse {
    private final List<VideoResponse> items;
    private final String nextPageToken;
}
