package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConditionalOnBean(BatchQueueTrendingCollector.class)
@Component
@RequiredArgsConstructor
public class BatchQueueVideoCollector {
    private final VideoUncollectedTrendingQueue videoUncollectedTrendingQueue;
    private final VideoCollectedTrendingQueue videoCollectedTrendingQueue;
    private final BatchVideoCollector batchVideoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final VideoReader videoReader;

    public void collect() {
        int availableVideoChannelToken = 10000;

        while (availableVideoChannelToken > 0 && !videoUncollectedTrendingQueue.isEmpty()) {
            int videoChannelFetchCount = Math.min(youtubeDataApiProperties.getMaxFetchCount(), availableVideoChannelToken / 2);

            List<RegionTrendingItem> frontRegionTrendingItems = new ArrayList<>();
            while (!videoUncollectedTrendingQueue.isEmpty() && frontRegionTrendingItems.size() < videoChannelFetchCount) {
                List<RegionTrendingItem> regionTrendingItems = new ArrayList<>();

                for (int i = 0; i < videoChannelFetchCount - frontRegionTrendingItems.size(); i++) {
                    regionTrendingItems.add(videoUncollectedTrendingQueue.poll());
                }

                List<String> videoYoutubeIds = regionTrendingItems.stream()
                        .map(RegionTrendingItem::getVideoYoutubeId)
                        .toList();

                List<String> existingVideoYoutubeIds = videoReader.readByYoutubeIds(videoYoutubeIds).stream().map(Video::getYoutubeId).toList();

                regionTrendingItems.forEach(regionTrendingItem -> {
                    if (existingVideoYoutubeIds.contains(regionTrendingItem.getVideoYoutubeId())) {
                        videoCollectedTrendingQueue.add(regionTrendingItem);
                    } else {
                        frontRegionTrendingItems.add(regionTrendingItem);
                    }
                });
            }

            List<String> videoYoutubeIds = frontRegionTrendingItems.stream()
                    .map(RegionTrendingItem::getVideoYoutubeId)
                    .toList();

            batchVideoCollector.collect(videoYoutubeIds);

            frontRegionTrendingItems.forEach(videoCollectedTrendingQueue::add);
        }
    }
}
