package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.YoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.youtubetimelapse.region.domain.Region;
import io.github.hamsteak.youtubetimelapse.region.domain.RegionReader;
import io.github.hamsteak.youtubetimelapse.video.domain.BatchVideoCollector;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.hamsteak.youtubetimelapse.config.Constants.MAX_FETCH_COUNT;

/**
 * Trending 목록 조회 -> (DB에 없는 Video 조회 -> (DB에 없는 Channel 조회 -> 조회한 Channel 하나씩 삽입) -> 조회한 Video 하나씩 삽입) -> 조회한 Trending 하나씩 삽입
 * API 호출 횟수: Trending(1) + Video(1) + Channel(1)
 * DB 쿼리 횟수: Trending(select-video:N + insert:N) + Video(select-in:1 + select-channel:N + insert:N) + Channel(select-in:1 + insert:N)
 */
@Primary
@Component
@RequiredArgsConstructor
public class BatchTrendingCollector implements TrendingCollector {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final BatchVideoCollector batchVideoCollector;
    private final TrendingCreator trendingCreator;
    private final VideoReader videoReader;
    private final RegionReader regionReader;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount, long regionId) {
        Region region = regionReader.read(regionId);

        List<String> videoYoutubeIds = new ArrayList<>();

        String pageToken = null;
        int remainCount = collectCount;
        while (remainCount > 0) {
            remainCount -= MAX_FETCH_COUNT;

            VideoListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(MAX_FETCH_COUNT, region.getRegionCode(), pageToken);
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
                    trendingCreator.create(dateTime, videoId, i + 1, regionId);
                });
    }
}
