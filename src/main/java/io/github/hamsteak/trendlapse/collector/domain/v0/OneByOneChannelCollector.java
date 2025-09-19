package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.channel.domain.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.domain.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.ChannelItem;
import io.github.hamsteak.trendlapse.collector.fetcher.ChannelFetcher;
import io.github.hamsteak.trendlapse.collector.storer.ChannelStorer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "channel-strategy", havingValue = "one-by-one")
@Component
@RequiredArgsConstructor
public class OneByOneChannelCollector implements ChannelCollector {
    private final ChannelFinder channelFinder;
    private final ChannelFetcher channelFetcher;
    private final ChannelStorer channelStorer;

    @Override
    public int collect(List<String> channelYoutubeIds) {
        channelYoutubeIds = channelFinder.findMissingChannelYoutubeIds(channelYoutubeIds.stream().distinct().toList());

        List<ChannelItem> channelItems = channelFetcher.fetch(channelYoutubeIds, 1);
        return channelStorer.store(channelItems);
    }
}
