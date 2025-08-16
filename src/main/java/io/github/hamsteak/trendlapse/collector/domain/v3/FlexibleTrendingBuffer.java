package io.github.hamsteak.trendlapse.collector.domain.v3;

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

    public List<TrendingItem> pollTrendingVideoYoutubeIds(int maxVideoFetchCount) {
        List<TrendingItem> polledTrendingItems = new ArrayList<>();

        int missingVideoCount = 0;
        while (!uncollectedTrendingItemQueue.isEmpty() && missingVideoCount < maxVideoFetchCount) {
            TrendingItem polledTrendingItem = uncollectedTrendingItemQueue.poll();

            if (!videoFinder.existsByYoutubeId(polledTrendingItem.getVideoYoutubeId())) {
                missingVideoCount++;
            }

            polledTrendingItems.add(polledTrendingItem);
        }

        return polledTrendingItems;
    }
}
