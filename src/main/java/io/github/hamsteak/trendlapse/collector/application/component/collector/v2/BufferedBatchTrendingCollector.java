package io.github.hamsteak.trendlapse.collector.application.component.collector.v2;

import io.github.hamsteak.trendlapse.collector.application.component.collector.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.VideoCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.TrendingFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.TrendingStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "trending-strategy", havingValue = "buffered-batch")
@Slf4j
@Component
@RequiredArgsConstructor
public class BufferedBatchTrendingCollector implements TrendingCollector {
    private final TrendingFetcher trendingFetcher;
    private final TrendingStorer trendingStorer;
    private final VideoCollector videoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;

    @Override
    public void collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        List<TrendingItem> fetchedTrendingItems = new ArrayList<>();

        fetchedTrendingItems.addAll(trendingFetcher.fetch(dateTime, collectSize, regionCodes, youtubeDataApiProperties.getMaxResultCount()));

        List<String> videoYoutubeIds = fetchedTrendingItems.stream().map(TrendingItem::getVideoYoutubeId).toList();
        videoCollector.collect(videoYoutubeIds);

        trendingStorer.store(fetchedTrendingItems);
    }
}
