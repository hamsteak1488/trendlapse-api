package io.github.hamsteak.trendlapse.youtube.infrastructure.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RawVideoListResponse {
    private final List<RawVideo> items;
    private final String nextPageToken;
}
