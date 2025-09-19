package io.github.hamsteak.trendlapse.collector.storer;

import io.github.hamsteak.trendlapse.collector.domain.VideoItem;

import java.util.List;

public interface VideoStorer {
    int store(List<VideoItem> videoItems);
}
