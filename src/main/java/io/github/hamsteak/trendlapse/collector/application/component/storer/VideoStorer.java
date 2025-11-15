package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.collector.application.dto.VideoItem;

import java.util.List;

public interface VideoStorer {
    int store(List<VideoItem> videoItems);
}
