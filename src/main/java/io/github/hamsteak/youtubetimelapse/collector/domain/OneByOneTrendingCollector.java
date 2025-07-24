package io.github.hamsteak.youtubetimelapse.collector.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.youtubetimelapse.region.domain.Region;
import io.github.hamsteak.youtubetimelapse.region.domain.RegionReader;
import io.github.hamsteak.youtubetimelapse.trending.domain.TrendingPutter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Trending 목록 조회 -> Trending 하나씩 삽입 (Trending 만드는데 Video 없다면 생성 (Video 만드는데 Channel 없다면 생성) )
 * API 호출 횟수: Trending(1) + Video(N) + Channel(N)
 * DB 쿼리 횟수: Trending(insert:N) + Video(select:N + insert:N) + Channel(select:N + insert:N)
 */

//@Primary
@Component
@RequiredArgsConstructor
public class OneByOneTrendingCollector implements TrendingCollector {
    private final RegionReader regionReader;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final TrendingPutter trendingPutter;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount, long regionId) {
        Region region = regionReader.read(regionId);

        List<String> videoYoutubeIds = new ArrayList<>();

        String pageToken = null;
        int remainCount = collectCount;
        while (remainCount > 0) {
            remainCount -= youtubeDataApiProperties.getMaxFetchCount();

            VideoListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(youtubeDataApiProperties.getMaxFetchCount(), region.getRegionCode(), pageToken);
            trendingListResponse.getItems().stream()
                    .map(VideoResponse::getId)
                    .forEach(videoYoutubeIds::add);

            if (trendingListResponse.getNextPageToken() == null) {
                break;
            }
            pageToken = trendingListResponse.getNextPageToken();
        }

        IntStream.range(0, videoYoutubeIds.size())
                .forEach(i -> trendingPutter.put(dateTime, videoYoutubeIds.get(i), i + 1, regionId));
    }
}
