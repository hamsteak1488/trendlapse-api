package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

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
            int videoChannelFetchCount = Math.min(youtubeDataApiProperties.getMaxFetchCount(),
                    Math.min(availableVideoChannelToken / 2, videoUncollectedTrendingQueue.size()));

            List<VideoUncollectedTrendingQueue.RegionTrendingItem> frontRegionTrendingItems = IntStream.range(0, videoChannelFetchCount)
                    .mapToObj(i -> videoUncollectedTrendingQueue.poll())
                    .toList();

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
