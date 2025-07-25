package io.github.hamsteak.trendlapse.external.youtube.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ChannelListResponse {
    private final List<ChannelResponse> items;
    private final String nextPageToken;
}
