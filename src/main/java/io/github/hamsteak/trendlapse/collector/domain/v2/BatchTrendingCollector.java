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
import java.util.*;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class BatchTrendingCollector implements TrendingCollector {
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
        Map<Long, List<String>> videoYoutubeIds = new HashMap<>();

        regionReader.read(regionIds)
                .forEach(region -> {
                    List<String> regionVideoYoutubeIds = new ArrayList<>();

                    String pageToken = null;
                    int remainCount = collectCount;
                    while (remainCount > 0) {
                        remainCount -= youtubeDataApiProperties.getMaxResultCount();

                        TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(youtubeDataApiProperties.getMaxResultCount(), region.getRegionCode(), pageToken);
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

                    videoYoutubeIds.put(region.getId(), regionVideoYoutubeIds);
                });

        batchVideoCollector.collect(
                videoYoutubeIds.values().stream()
                        .flatMap(Collection::stream).toList()
        );

        videoYoutubeIds.forEach(
                (regionId, regionVideoYoutubeIds) -> IntStream.range(0, regionVideoYoutubeIds.size())
                        .forEach(i -> {
                            long videoId = videoReader.readByYoutubeId(regionVideoYoutubeIds.get(i)).getId();
                            trendingCreator.create(dateTime, videoId, i + 1, regionId);
                        })
        );

    }
}
