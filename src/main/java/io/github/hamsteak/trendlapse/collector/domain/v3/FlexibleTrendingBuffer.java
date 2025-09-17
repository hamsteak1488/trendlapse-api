package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlexibleTrendingBuffer {
    private final Queue<TrendingItem> uncollectedTrendingItemQueue = new ArrayDeque<>();
    private final VideoFinder videoFinder;

    public void pushTrendingItem(TrendingItem item) {
        uncollectedTrendingItemQueue.add(item);
    }

    public void pushTrendingItems(List<TrendingItem> items) {
        uncollectedTrendingItemQueue.addAll(items);
    }

    public List<TrendingItem> pollTrendingItems(int maxPollCount) {
        List<TrendingItem> polledTrendingItems = new ArrayList<>();

        log.info("Starting to poll trending items from uncollected queue. (Max video poll count: {})", maxPollCount);

        int missingCount = 0;
        while (!uncollectedTrendingItemQueue.isEmpty() && missingCount < maxPollCount) {
            TrendingItem polledTrendingItem = uncollectedTrendingItemQueue.poll();

            if (!videoFinder.existsByYoutubeId(polledTrendingItem.getVideoYoutubeId())) {
                missingCount++;
            }

            polledTrendingItems.add(polledTrendingItem);
        }

        log.info("Polled {} trending items from uncollected queue. {} items require video and channel data fetch.",
                polledTrendingItems.size(), missingCount);

        return polledTrendingItems;
    }
}
