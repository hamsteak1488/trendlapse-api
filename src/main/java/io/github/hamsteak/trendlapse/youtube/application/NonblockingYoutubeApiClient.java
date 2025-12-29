package io.github.hamsteak.trendlapse.youtube.application;

import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawRegionListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawVideoListResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface NonblockingYoutubeApiClient {
    Mono<RawChannelListResponse> fetchChannels(List<String> channelYoutubeIds);

    Mono<RawVideoListResponse> fetchVideos(List<String> videoYoutubeIds);

    Mono<RawVideoListResponse> fetchTrendings(String regionCode, String pageToken, int maxResultCount);

    Mono<RawRegionListResponse> fetchRegions();
}
