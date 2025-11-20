package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RegionListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.VideoListResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface NonblockingYoutubeDataApiCaller {
    Mono<ChannelListResponse> fetchChannels(List<String> channelYoutubeId);

    Mono<VideoListResponse> fetchVideos(List<String> videoYoutubeId);

    Mono<TrendingListResponse> fetchTrendings(int maxResultCount, String regionCode, String pageToken);

    Mono<RegionListResponse> fetchRegions();
}
