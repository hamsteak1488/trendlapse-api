package io.github.hamsteak.trendlapse.collector.application.component.collector;

import java.util.List;

public interface ChannelCollector {
    int collect(List<String> channelYoutubeIds);
}
