package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.common.errors.exception.VideoNotFoundException;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
                    int remainCollectCount = collectCount;
                    while (remainCollectCount > 0) {
                        int maxResultCount = Math.min(remainCollectCount, youtubeDataApiProperties.getMaxResultCount());

                        TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(maxResultCount, region.getRegionCode(), pageToken);
                        regionVideoYoutubeIds.addAll(
                                trendingListResponse.getItems().stream()
                                        .map(VideoResponse::getId)
                                        .toList()
                        );

                        if (trendingListResponse.getNextPageToken() == null) {
                            break;
                        }
                        pageToken = trendingListResponse.getNextPageToken();

                        remainCollectCount -= maxResultCount;
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
            String videoYoutubeId = collectedItem.getVideoYoutubeId();
            try {
                long videoId = videoReader.readByYoutubeId(videoYoutubeId).getId();
                trendingCreator.create(dateTime, videoId, collectedItem.getRank(), collectedItem.getRegionId());
            } catch (VideoNotFoundException ex) {
                log.warn("Cannot find video despite video collection tasks. (Trending={}, Video Youtube Id={})", collectedItem, videoYoutubeId, ex);
            }
        }
    }
}
