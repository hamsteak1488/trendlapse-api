package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.VideoCollector;
import io.github.hamsteak.trendlapse.common.errors.exception.VideoNotFoundException;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Trending 목록 조회 -> (DB에 없는 Video 조회 -> (DB에 없는 Channel 조회 -> 조회한 Channel 하나씩 삽입) -> 조회한 Video 하나씩 삽입) -> 조회한 Trending 하나씩 삽입
 * API 호출 횟수: Trending(1) + Video(1) + Channel(1)
 * DB 쿼리 횟수: Trending(select-video:N + insert:N) + Video(select-in:1 + select-channel:N + insert:N) + Channel(select-in:1 + insert:N)
 */
@ConditionalOnProperty(prefix = "collector", name = "trending-strategy", havingValue = "batch")
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchTrendingCollector implements TrendingCollector {
    private final VideoCollector videoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final TrendingCreator trendingCreator;

    @Override
    public int collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        int storedCount = 0;

        for (String regionCode : regionCodes) {
            List<VideoResponse> trendingVideoResponses = fetchTrendings(collectSize, regionCode);

            List<String> videoYoutubeIds = trendingVideoResponses.stream().map(VideoResponse::getId).toList();
            videoCollector.collect(videoYoutubeIds);

            storedCount += storeFromResponses(dateTime, regionCode, trendingVideoResponses);
        }

        return storedCount;
    }

    private List<VideoResponse> fetchTrendings(int collectSize, String regionCode) {
        List<VideoResponse> responses = new ArrayList<>();

        String pageToken = null;
        int remainingCount = collectSize;
        while (remainingCount > 0) {
            int maxResultCount = Math.min(remainingCount, youtubeDataApiProperties.getMaxResultCount());

            TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(maxResultCount, regionCode, pageToken);
            responses.addAll(trendingListResponse.getItems());

            if (trendingListResponse.getNextPageToken() == null) {
                break;
            }
            pageToken = trendingListResponse.getNextPageToken();

            remainingCount -= youtubeDataApiProperties.getMaxResultCount();
        }

        return responses;
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
