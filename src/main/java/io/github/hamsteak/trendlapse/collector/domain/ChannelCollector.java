package io.github.hamsteak.trendlapse.collector.domain;

import java.util.List;

public interface ChannelCollector {
    int collect(List<String> channelYoutubeIds);
}
