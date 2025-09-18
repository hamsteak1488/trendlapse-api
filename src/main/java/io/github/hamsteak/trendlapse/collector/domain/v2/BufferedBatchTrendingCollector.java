package io.github.hamsteak.trendlapse.collector.domain.v2;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;
import io.github.hamsteak.trendlapse.collector.domain.VideoCollector;
import io.github.hamsteak.trendlapse.collector.fetcher.TrendingFetcher;
import io.github.hamsteak.trendlapse.collector.storer.TrendingStorer;
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

    @Override
    public void collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        List<TrendingItem> fetchedTrendingItems = new ArrayList<>();

        fetchedTrendingItems.addAll(trendingFetcher.fetch(dateTime, collectSize, regionCodes));

        List<String> videoYoutubeIds = fetchedTrendingItems.stream().map(TrendingItem::getVideoYoutubeId).toList();
        videoCollector.collect(videoYoutubeIds);

        trendingStorer.store(fetchedTrendingItems);
    }
}
