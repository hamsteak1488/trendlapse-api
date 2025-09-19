package io.github.hamsteak.trendlapse.collector.fetcher;

import io.github.hamsteak.trendlapse.collector.domain.ChannelItem;

import java.util.List;

public interface ChannelFetcher {
    List<ChannelItem> fetch(List<String> channelYoutubeIds, int maxResultCount);
}
