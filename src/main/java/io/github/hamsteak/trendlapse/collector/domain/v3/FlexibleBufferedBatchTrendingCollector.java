package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;
import io.github.hamsteak.trendlapse.collector.domain.VideoCollector;
import io.github.hamsteak.trendlapse.collector.fetcher.TrendingFetcher;
import io.github.hamsteak.trendlapse.collector.storer.TrendingStorer;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
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
        for (String regionCode : regionCodes) {
            List<TrendingItem> fetchedTrendingItems = trendingFetcher.fetch(dateTime, collectSize, regionCode);
            flexibleTrendingBuffer.pushTrendingItems(fetchedTrendingItems);
            pushedCount += fetchedTrendingItems.size();
        }
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
