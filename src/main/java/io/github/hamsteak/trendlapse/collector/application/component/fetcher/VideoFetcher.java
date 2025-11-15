package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.VideoItem;

import java.util.List;

public interface VideoFetcher {
    List<VideoItem> fetch(List<String> videoYoutubeIds, int maxResultCount);
}
