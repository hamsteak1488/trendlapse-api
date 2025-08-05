package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchQueueTrendingCollector implements TrendingCollector {
    private final RegionReader regionReader;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final VideoUncollectedTrendingQueue videoUncollectedTrendingQueue;
    private final BatchQueueVideoCollector batchQueueVideoCollector;
    private final VideoCollectedTrendingQueue videoCollectedTrendingQueue;
    private final TrendingCreator trendingCreator;
    private final VideoReader videoReader;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount, List<Long> regionIds) {
        regionReader.read(regionIds)
                .forEach(region -> {
                    List<String> regionVideoYoutubeIds = new ArrayList<>();

                    String pageToken = null;
                    int remainCount = collectCount;
                    while (remainCount > 0) {
                        remainCount -= youtubeDataApiProperties.getMaxFetchCount();

                        TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(youtubeDataApiProperties.getMaxFetchCount(), region.getRegionCode(), pageToken);
                        regionVideoYoutubeIds.addAll(
                                trendingListResponse.getItems().stream()
                                        .map(VideoResponse::getId)
                                        .toList()
                        );

                        if (trendingListResponse.getNextPageToken() == null) {
                            break;
                        }
                        pageToken = trendingListResponse.getNextPageToken();
                    }

                    for (int i = 0; i < regionVideoYoutubeIds.size(); i++) {
                        int rank = i + 1;
                        String videoYoutubeId = regionVideoYoutubeIds.get(i);
                        videoUncollectedTrendingQueue.add(new TrendingItem(region.getId(), rank, videoYoutubeId));
                    }
                });

        batchQueueVideoCollector.collect();

        while (!videoCollectedTrendingQueue.isEmpty()) {
            TrendingItem collectedItem = videoCollectedTrendingQueue.poll();
            long videoId = videoReader.readByYoutubeId(collectedItem.getVideoYoutubeId()).getId();

            trendingCreator.create(dateTime, videoId, collectedItem.getRank(), collectedItem.getRegionId());
        }
    }
}
