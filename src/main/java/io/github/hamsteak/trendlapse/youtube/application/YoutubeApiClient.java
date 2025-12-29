package io.github.hamsteak.trendlapse.youtube.application;

import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawRegionListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawVideoListResponse;

import java.util.List;

public interface YoutubeApiClient {
    RawChannelListResponse fetchChannels(List<String> channelYoutubeIds);

    RawVideoListResponse fetchVideos(List<String> videoYoutubeIds);

    RawVideoListResponse fetchTrendings(String regionCode, String pageToken, int maxResultCount);

    RawRegionListResponse fetchRegions();
}
