package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;

import java.util.List;

public interface ChannelFetcher {
    List<ChannelItem> fetch(List<String> channelYoutubeIds, int maxResultCount);
}
