package io.github.hamsteak.trendlapse.collector.domain.v1;

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
import java.util.List;

/**
 * Trending 목록 조회 -> (DB에 없는 Video 조회 -> (DB에 없는 Channel 조회 -> 조회한 Channel 하나씩 삽입) -> 조회한 Video 하나씩 삽입) -> 조회한 Trending 하나씩 삽입
 * API 호출 횟수: Trending(1) + Video(1) + Channel(1)
 * DB 쿼리 횟수: Trending(select-video:N + insert:N) + Video(select-in:1 + select-channel:N + insert:N) + Channel(select-in:1 + insert:N)
 */
@ConditionalOnProperty(prefix = "collector", name = "trending-strategy", havingValue = "batch")
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchTrendingCollector implements TrendingCollector {
    private final TrendingFetcher trendingFetcher;
    private final TrendingStorer trendingStorer;
    private final VideoCollector videoCollector;

    @Override
    public void collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        for (String regionCode : regionCodes) {
            List<TrendingItem> fetchedTrendingItems = trendingFetcher.fetch(dateTime, collectSize, List.of(regionCode));
            List<String> videoYoutubeIds = fetchedTrendingItems.stream().map(TrendingItem::getVideoYoutubeId).toList();

            videoCollector.collect(videoYoutubeIds);
            trendingStorer.store(fetchedTrendingItems);
        }
    }
}
