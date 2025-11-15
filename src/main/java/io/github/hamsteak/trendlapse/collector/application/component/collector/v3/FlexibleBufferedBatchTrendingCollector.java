package io.github.hamsteak.trendlapse.collector.application.component.collector.v3;

import io.github.hamsteak.trendlapse.collector.application.component.collector.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.collector.application.component.collector.VideoCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.TrendingFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.TrendingStorer;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "trending-strategy", havingValue = "flexible-buffered-batch", matchIfMissing = true)
@Slf4j
@Component
@RequiredArgsConstructor
public class FlexibleBufferedBatchTrendingCollector implements TrendingCollector {
    private final TrendingFetcher trendingFetcher;
    private final TrendingStorer trendingStorer;
    private final VideoCollector videoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final FlexibleTrendingBuffer flexibleTrendingBuffer;

    @Override
    public void collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        int pushedCount = 0;
        List<TrendingItem> fetchedTrendingItems = trendingFetcher.fetch(dateTime, collectSize, regionCodes, youtubeDataApiProperties.getMaxResultCount());
        flexibleTrendingBuffer.pushTrendingItems(fetchedTrendingItems);
        pushedCount += fetchedTrendingItems.size();
        log.info("Pushed {} trending items.", pushedCount);

        int availableTokenCountForVideoAndChannel = 5000;
        List<TrendingItem> polledTrendingItems = flexibleTrendingBuffer.pollTrendingItems(
                availableTokenCountForVideoAndChannel * youtubeDataApiProperties.getMaxResultCount());
        log.info("Polled {} trending items.", polledTrendingItems.size());

        List<String> videoYoutubeIds = polledTrendingItems.stream()
                .map(TrendingItem::getVideoYoutubeId)
                .toList();
        videoCollector.collect(videoYoutubeIds);

        trendingStorer.store(polledTrendingItems);
    }
}
