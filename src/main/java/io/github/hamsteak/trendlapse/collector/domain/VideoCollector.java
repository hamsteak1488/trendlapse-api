package io.github.hamsteak.trendlapse.collector.domain;

import java.util.List;

public interface VideoCollector {
    int collect(List<String> videoYoutubeIds);
}
