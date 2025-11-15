package io.github.hamsteak.trendlapse.collector.application.component.collector.v1;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.application.component.collector.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.ChannelFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.ChannelStorer;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
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
    private final YoutubeDataApiProperties youtubeDataApiProperties;

    public int collect(List<String> channelYoutubeIds) {
        channelYoutubeIds = channelFinder.findMissingChannelYoutubeIds(channelYoutubeIds.stream().distinct().toList());

        log.info("Found {} missing channels.", channelYoutubeIds.size());

        List<ChannelItem> channelItems = channelFetcher.fetch(channelYoutubeIds, youtubeDataApiProperties.getMaxResultCount());

        int storedCount = channelStorer.store(channelItems);

        log.info("Stored {} channels.", storedCount);

        return storedCount;
    }
}
