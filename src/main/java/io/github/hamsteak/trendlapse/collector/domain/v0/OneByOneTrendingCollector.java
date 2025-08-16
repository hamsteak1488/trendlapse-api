package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.common.errors.exception.VideoNotFoundException;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Trending 목록 조회 -> Trending 하나씩 삽입 (Trending 만드는데 Video 없다면 생성 (Video 만드는데 Channel 없다면 생성) )
 * API 호출 횟수: Trending(1) + Video(N) + Channel(N)
 * DB 쿼리 횟수: Trending(insert:N) + Video(select:N + insert:N) + Channel(select:N + insert:N)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OneByOneTrendingCollector implements TrendingCollector {
    private final OneByOneVideoCollector oneByOneVideoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final TrendingCreator trendingCreator;

    @Override
    public int collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        int collectedCount = 0;

        for (String regionCode : regionCodes) {
            List<VideoResponse> trendingVideoResponses = fetchTrendings(collectSize, regionCode);

            List<String> trendingVideoYoutubeIds = trendingVideoResponses.stream().map(VideoResponse::getId).toList();
            oneByOneVideoCollector.collect(trendingVideoYoutubeIds);

            collectedCount += storeFromResponses(dateTime, regionCode, trendingVideoResponses);
        }

        return collectedCount;
    }

    private List<VideoResponse> fetchTrendings(int collectSize, String regionCode) {
        List<VideoResponse> trendingVideoResponses = new ArrayList<>();

        String pageToken = null;
        int remainingCount = collectSize;
        while (remainingCount > 0) {
            int maxResultCount = Math.min(remainingCount, youtubeDataApiProperties.getMaxResultCount());

            TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(maxResultCount, regionCode, pageToken);
            trendingVideoResponses.addAll(trendingListResponse.getItems());

            if (trendingListResponse.getNextPageToken() == null) {
                break;
            }
            pageToken = trendingListResponse.getNextPageToken();

            remainingCount -= youtubeDataApiProperties.getMaxResultCount();
        }

        return trendingVideoResponses;
    }

    private int storeFromResponses(LocalDateTime dateTime, String regionCode, List<VideoResponse> trendingVideoResponses) {
        int storedCount = 0;

        for (int i = 0; i < trendingVideoResponses.size(); i++) {
            int rank = i + 1;
            String videoYoutubeId = trendingVideoResponses.get(i).getId();

            try {
                trendingCreator.create(dateTime, videoYoutubeId, rank, regionCode);
                storedCount++;
            } catch (VideoNotFoundException ex) {
                log.info("Skipping trending record creation: No matching video found (region={}, rank={}, videoYoutubeId={}).",
                        regionCode, rank, videoYoutubeId);
            }
        }

        return storedCount;
    }
}
