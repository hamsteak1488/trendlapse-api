package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import io.github.hamsteak.trendlapse.external.youtube.dto.*;

import java.util.List;

public interface YoutubeDataApiCaller {
    ChannelResponse fetchChannel(String channelYoutubeId);

    ChannelListResponse fetchChannels(List<String> channelYoutubeId);

    VideoResponse fetchVideo(String videoYoutubeId);

    VideoListResponse fetchVideos(List<String> videoYoutubeId);

    TrendingListResponse fetchTrendings(int count, String regionCode, String pageToken);
}
