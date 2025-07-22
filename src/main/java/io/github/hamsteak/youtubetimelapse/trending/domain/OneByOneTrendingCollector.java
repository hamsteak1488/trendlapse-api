package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.RestTemplateYoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.youtubetimelapse.region.domain.Region;
import io.github.hamsteak.youtubetimelapse.region.domain.RegionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.hamsteak.youtubetimelapse.config.Constants.MAX_FETCH_COUNT;

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
    private final RestTemplateYoutubeDataApiCaller youtubeDataApiCaller;
    private final TrendingPutter trendingPutter;

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

        IntStream.range(0, videoYoutubeIds.size())
                .forEach(i -> trendingPutter.put(dateTime, videoYoutubeIds.get(i), i + 1, regionId));
    }
}
