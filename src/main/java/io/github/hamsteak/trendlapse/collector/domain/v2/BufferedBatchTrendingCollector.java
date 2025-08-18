package io.github.hamsteak.trendlapse.collector.domain.v2;

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
import java.util.*;

@ConditionalOnProperty(prefix = "collector", name = "trending-strategy", havingValue = "buffered-batch")
@Slf4j
@Component
@RequiredArgsConstructor
public class BufferedBatchTrendingCollector implements TrendingCollector {
    private final VideoCollector videoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final TrendingCreator trendingCreator;

    @Override
    public int collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        int storedCount = 0;

        Map<String, List<VideoResponse>> trendingVideoResponseBufferMap = new HashMap<>();
        for (String regionCode : regionCodes) {
            trendingVideoResponseBufferMap.put(regionCode, fetchTrendings(collectSize, regionCode));
        }

        videoCollector.collect(
                trendingVideoResponseBufferMap.values().stream()
                        .flatMap(Collection::stream)
                        .map(VideoResponse::getId).toList()
        );

        for (Map.Entry<String, List<VideoResponse>> entry : trendingVideoResponseBufferMap.entrySet()) {
            String regionCode = entry.getKey();
            List<VideoResponse> trendingVideoResponseBuffer = entry.getValue();

            storedCount += storeFromResponses(dateTime, regionCode, trendingVideoResponseBuffer);
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
