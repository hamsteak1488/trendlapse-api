package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.Video;
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
@Component
@RequiredArgsConstructor
public class OneByOneTrendingCollector implements TrendingCollector {
    private final RegionReader regionReader;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final TrendingCreator trendingCreator;
    private final OneByOneVideoCollector oneByOneVideoCollector;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount, List<Long> regionIds) {
        regionReader.read(regionIds)
                .forEach(region -> {
                    List<String> videoYoutubeIds = new ArrayList<>();

                    String pageToken = null;
                    int remainCount = collectCount;
                    while (remainCount > 0) {
                        remainCount -= youtubeDataApiProperties.getMaxResultCount();

                        TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(youtubeDataApiProperties.getMaxResultCount(), region.getRegionCode(), pageToken);
                        trendingListResponse.getItems().stream()
                                .map(VideoResponse::getId)
                                .forEach(videoYoutubeIds::add);

                        if (trendingListResponse.getNextPageToken() == null) {
                            break;
                        }
                        pageToken = trendingListResponse.getNextPageToken();
                    }


                    IntStream.range(0, videoYoutubeIds.size())
                            .forEach(i -> {
                                Video trendingVideo = oneByOneVideoCollector.collect(videoYoutubeIds.get(i));
                                trendingCreator.create(dateTime, trendingVideo.getId(), i + 1, region.getId());
                            });
                });
    }
}
