package io.github.hamsteak.trendlapse.collector.storer;

import io.github.hamsteak.trendlapse.collector.domain.ChannelItem;

import java.util.List;

public interface ChannelStorer {
    int store(List<ChannelItem> channelItems);
}
