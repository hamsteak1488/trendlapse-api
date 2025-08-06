package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@ConditionalOnBean(BatchQueueTrendingCollector.class)
@Component
@RequiredArgsConstructor
public class BatchQueueVideoCollector {
    private final VideoUncollectedTrendingQueue videoUncollectedTrendingQueue;
    private final VideoCollectedTrendingQueue videoCollectedTrendingQueue;
    private final BatchVideoCollector batchVideoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final VideoFinder videoFinder;

    public void collect() {
        int availableVideoChannelToken = 10000;

        // 제외한 여분 토큰이 있고 미수집 TredingItem이 있을 경우, 반복적으로 수집 작업 진행.
        while (availableVideoChannelToken > 0 && !videoUncollectedTrendingQueue.isEmpty()) {
            int fetchCount = Math.min(availableVideoChannelToken / 2, youtubeDataApiProperties.getMaxResultCount());

            log.debug("fetchCount={} (Minimum value among token / 2=({}), maxFetchCount({}))",
                    fetchCount, availableVideoChannelToken / 2, youtubeDataApiProperties.getMaxResultCount());

            List<TrendingItem> trendingItemsToFetches = new ArrayList<>();

            // 수집 요청할 목록이 다 채워지지 않았을 경우, 미수집 큐에서 꺼내오기
            while (!videoUncollectedTrendingQueue.isEmpty() && trendingItemsToFetches.size() < fetchCount) {
                List<TrendingItem> frontTrendingItems = new ArrayList<>();

                int pollCount = Math.min(videoUncollectedTrendingQueue.size(), fetchCount - trendingItemsToFetches.size());
                log.debug("There will be {} poll tasks. (Minimum value among videoUncollectedTrendingQueue.size({}), fetchCount({}) - trendingItemsToFetches.size({}))",
                        pollCount, videoUncollectedTrendingQueue.size(), fetchCount, trendingItemsToFetches.size());

                IntStream.range(0, pollCount)
                        .forEach(i -> frontTrendingItems.add(videoUncollectedTrendingQueue.poll()));

                List<String> missingVideoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(
                        frontTrendingItems.stream()
                                .map(TrendingItem::getVideoYoutubeId)
                                .toList());

                // DB 존재 여부에 따라 'Fetch 목록' 혹은 '수집 완료 Queue'에 추가.
                frontTrendingItems.forEach(trendingItem -> {
                    if (missingVideoYoutubeIds.contains(trendingItem.getVideoYoutubeId())) {
                        trendingItemsToFetches.add(trendingItem);
                    } else {
                        videoCollectedTrendingQueue.add(trendingItem);
                    }
                });
            }

            batchVideoCollector.collect(trendingItemsToFetches.stream()
                    .map(TrendingItem::getVideoYoutubeId)
                    .toList());

            trendingItemsToFetches.forEach(videoCollectedTrendingQueue::add);
        }
    }
}
