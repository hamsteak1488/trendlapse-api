package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Trending 목록 조회 -> (DB에 없는 Video 조회 -> (DB에 없는 Channel 조회 -> 조회한 Channel 하나씩 삽입) -> 조회한 Video 하나씩 삽입) -> 조회한 Trending 하나씩 삽입
 * API 호출 횟수: Trending(1) + Video(1) + Channel(1)
 * DB 쿼리 횟수: Trending(select-video:N + insert:N) + Video(select-in:1 + select-channel:N + insert:N) + Channel(select-in:1 + insert:N)
 */
@RequiredArgsConstructor
public class BatchTrendingCollector implements TrendingCollector {
    private final RegionReader regionReader;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final BatchVideoCollector batchVideoCollector;
    private final TrendingCreator trendingCreator;
    private final VideoReader videoReader;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount, List<Long> regionIds) {
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

                    batchVideoCollector.collect(videoYoutubeIds);

                    IntStream.range(0, videoYoutubeIds.size())
                            .forEach(i -> {
                                long videoId = videoReader.readByYoutubeId(videoYoutubeIds.get(i)).getId();
                                trendingCreator.create(dateTime, videoId, i + 1, region.getId());
                            });
                });
    }
}
