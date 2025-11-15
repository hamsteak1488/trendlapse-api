package io.github.hamsteak.trendlapse.collector.application.component.collector;

import java.util.List;

public interface VideoCollector {
    int collect(List<String> videoYoutubeIds);
}
