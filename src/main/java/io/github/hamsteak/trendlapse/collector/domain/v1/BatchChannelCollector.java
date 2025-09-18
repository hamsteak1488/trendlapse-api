package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.channel.domain.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.domain.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.ChannelItem;
import io.github.hamsteak.trendlapse.collector.fetcher.ChannelFetcher;
import io.github.hamsteak.trendlapse.collector.storer.ChannelStorer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "channel-strategy", havingValue = "batch", matchIfMissing = true)
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchChannelCollector implements ChannelCollector {
    private final ChannelFinder channelFinder;
    private final ChannelFetcher channelFetcher;
    private final ChannelStorer channelStorer;

    public int collect(List<String> channelYoutubeIds) {
        channelYoutubeIds = channelFinder.findMissingChannelYoutubeIds(channelYoutubeIds.stream().distinct().toList());

        log.info("Found {} missing channels.", channelYoutubeIds.size());

        List<ChannelItem> channelItems = channelFetcher.fetch(channelYoutubeIds);

        int storedCount = channelStorer.store(channelItems);

        log.info("Stored {} channels.", storedCount);

        return storedCount;
    }
}
