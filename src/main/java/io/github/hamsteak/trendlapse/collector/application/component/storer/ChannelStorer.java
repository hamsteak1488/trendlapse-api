package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;

import java.util.List;

public interface ChannelStorer {
    int store(List<ChannelItem> channelItems);
}
