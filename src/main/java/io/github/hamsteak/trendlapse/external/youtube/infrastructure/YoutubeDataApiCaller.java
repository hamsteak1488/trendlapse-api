package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.RegionListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoListResponse;

import java.util.List;

public interface YoutubeDataApiCaller {
    ChannelListResponse fetchChannels(List<String> channelYoutubeId);

    VideoListResponse fetchVideos(List<String> videoYoutubeId);

    TrendingListResponse fetchTrendings(int maxResultCount, String regionCode, String pageToken);

    RegionListResponse fetchRegions();
}
