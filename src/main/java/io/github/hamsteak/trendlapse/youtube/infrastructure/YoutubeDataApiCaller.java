package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RegionListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.VideoListResponse;

import java.util.List;

public interface YoutubeDataApiCaller {
    ChannelListResponse fetchChannels(List<String> channelYoutubeId);

    VideoListResponse fetchVideos(List<String> videoYoutubeId);

    TrendingListResponse fetchTrendings(int maxResultCount, String regionCode, String pageToken);

    RegionListResponse fetchRegions();
}
