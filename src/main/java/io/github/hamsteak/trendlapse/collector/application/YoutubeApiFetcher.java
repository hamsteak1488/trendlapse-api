package io.github.hamsteak.trendlapse.collector.application;

import io.github.hamsteak.trendlapse.collector.application.dto.FetchedChannel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedRegion;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedVideo;

import java.util.List;
import java.util.Map;

public interface YoutubeApiFetcher {
    List<FetchedRegion> fetchRegions();

    List<FetchedChannel> fetchChannels(List<String> channelYoutubeIds);

    List<FetchedVideo> fetchVideos(List<String> videoYoutubeIds);

    Map<String, List<FetchedVideo>> fetchTrendingVideos(List<String> regionIds);
}
