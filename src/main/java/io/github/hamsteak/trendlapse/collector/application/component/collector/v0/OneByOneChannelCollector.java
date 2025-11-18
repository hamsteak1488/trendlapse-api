package io.github.hamsteak.trendlapse.collector.application.component.collector.v0;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.application.component.collector.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.ChannelFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.ChannelStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
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
