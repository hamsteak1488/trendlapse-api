package io.github.hamsteak.trendlapse.collector.application.component.collector.v0;

import io.github.hamsteak.trendlapse.collector.application.component.collector.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.collector.application.component.collector.VideoCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.TrendingFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.TrendingStorer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Trending 한개씩 조회 -> Trending 하나씩 삽입 (Trending 만드는데 Video 없다면 생성 (Video 만드는데 Channel 없다면 생성) )
 * API 호출 횟수: Trending(N) + Video(N) + Channel(N)
 * DB 쿼리 횟수: Trending(insert:N) + Video(select:N + insert:N) + Channel(select:N + insert:N)
 */
@ConditionalOnProperty(prefix = "collector", name = "trending-strategy", havingValue = "one-by-one")
@Slf4j
@Component
@RequiredArgsConstructor
public class OneByOneTrendingCollector implements TrendingCollector {
    private final TrendingFetcher trendingFetcher;
    private final TrendingStorer trendingStorer;
    private final VideoCollector videoCollector;

    @Override
    public void collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        for (String regionCode : regionCodes) {
            List<TrendingItem> fetchedTrendingItems = trendingFetcher.fetch(dateTime, collectSize, List.of(regionCode), 1);
            List<String> videoYoutubeIds = fetchedTrendingItems.stream().map(TrendingItem::getVideoYoutubeId).toList();

            videoCollector.collect(videoYoutubeIds);
            trendingStorer.store(fetchedTrendingItems);
        }
    }
}
