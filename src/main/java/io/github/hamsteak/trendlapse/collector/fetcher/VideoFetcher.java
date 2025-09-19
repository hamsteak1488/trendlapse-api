package io.github.hamsteak.trendlapse.collector.fetcher;

import io.github.hamsteak.trendlapse.collector.domain.VideoItem;

import java.util.List;

public interface VideoFetcher {
    List<VideoItem> fetch(List<String> videoYoutubeIds, int maxResultCount);
}
