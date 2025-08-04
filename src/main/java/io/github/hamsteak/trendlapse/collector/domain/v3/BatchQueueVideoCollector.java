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

            List<VideoUncollectedTrendingQueue.RegionTrendingItem> frontRegionTrendingItems = new ArrayList<>();
            while (!videoUncollectedTrendingQueue.isEmpty() && frontRegionTrendingItems.size() < videoChannelFetchCount) {
                List<VideoUncollectedTrendingQueue.RegionTrendingItem> regionTrendingItems = new ArrayList<>();

                for (int i = 0; i < videoChannelFetchCount - frontRegionTrendingItems.size(); i++) {
                    regionTrendingItems.add(videoUncollectedTrendingQueue.poll());
                }

                List<String> videoYoutubeIds = regionTrendingItems.stream()
                        .map(VideoUncollectedTrendingQueue.RegionTrendingItem::getVideoYoutubeId)
                        .toList();

                List<String> existingVideoYoutubeIds = videoReader.readByYoutubeIds(videoYoutubeIds).stream().map(Video::getYoutubeId).toList();

                frontRegionTrendingItems.addAll(regionTrendingItems.stream()
                        .filter(regionTrendingItem -> !existingVideoYoutubeIds.contains(regionTrendingItem.getVideoYoutubeId()))
                        .toList());
            }

            List<String> videoYoutubeIds = frontRegionTrendingItems.stream()
                    .map(VideoUncollectedTrendingQueue.RegionTrendingItem::getVideoYoutubeId)
                    .toList();

            batchVideoCollector.collect(videoYoutubeIds);

            List<Long> videoIds = videoReader.readByYoutubeIds(videoYoutubeIds).stream().map(Video::getId).toList();

            for (int i = 0; i < frontRegionTrendingItems.size(); i++) {
                VideoUncollectedTrendingQueue.RegionTrendingItem regionTrendingItem = frontRegionTrendingItems.get(i);
                long videoId = videoIds.get(i);

                videoCollectedTrendingQueue.add(
                        regionTrendingItem.getRegionId(),
                        regionTrendingItem.getRank(),
                        videoId
                );
            }
        }
    }
}
