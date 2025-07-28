package io.github.hamsteak.trendlapse.collector.domain.v2;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class BatchQueueTrendingCollector implements TrendingCollector {
    private final RegionReader regionReader;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final BatchVideoCollector batchVideoCollector;
    private final TrendingCreator trendingCreator;
    private final VideoReader videoReader;

    @Value(value = "${only-korea-region:false}")
    private boolean onlyKoreaRegion;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount, List<Long> regionIds) {
        Map<Long, List<String>> regionVideoYoutubeIds = new ConcurrentHashMap<>();

        regionReader.read(regionIds)
                .forEach(region -> {
                    List<String> videoYoutubeIds = new ArrayList<>();

                    String pageToken = null;
                    int remainCount = collectCount;
                    while (remainCount > 0) {
                        remainCount -= youtubeDataApiProperties.getMaxFetchCount();

                        TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(youtubeDataApiProperties.getMaxFetchCount(), region.getRegionCode(), pageToken);
                        trendingListResponse.getItems().stream()
                                .map(VideoResponse::getId)
                                .forEach(videoYoutubeIds::add);

                        if (trendingListResponse.getNextPageToken() == null) {
                            break;
                        }
                        pageToken = trendingListResponse.getNextPageToken();
                    }

                    regionVideoYoutubeIds.put(region.getId(), videoYoutubeIds);
                });

        batchVideoCollector.collect(
                regionVideoYoutubeIds.values().stream()
                        .flatMap(Collection::stream).toList()
        );

        regionVideoYoutubeIds.forEach(
                (regionId, videoYoutubeIds) -> IntStream.range(0, videoYoutubeIds.size())
                        .forEach(i -> {
                            long videoId = videoReader.readByYoutubeId(videoYoutubeIds.get(i)).getId();
                            trendingCreator.create(dateTime, videoId, i + 1, regionId);
                        })
        );

    }
}
