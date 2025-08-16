package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class FlexibleBufferedBatchTrendingCollector implements TrendingCollector {
    private final BatchVideoCollector batchVideoCollector;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final TrendingCreator trendingCreator;
    private final FlexibleTrendingBuffer flexibleTrendingBuffer;

    @Override
    public int collect(LocalDateTime dateTime, int collectSize, List<String> regionCodes) {
        for (String regionCode : regionCodes) {
            List<VideoResponse> trendingVideoResponses = fetchTrendings(collectSize, regionCode);

            for (int i = 0; i < trendingVideoResponses.size(); i++) {
                int rank = i + 1;
                String videoYoutubeId = trendingVideoResponses.get(i).getId();
                flexibleTrendingBuffer.pushTrendingItem(new TrendingItem(dateTime, regionCode, rank, videoYoutubeId));
            }
        }

        List<TrendingItem> lists = flexibleTrendingBuffer.pollTrendingVideoYoutubeIds(5000);

        batchVideoCollector.collect(lists.stream().map(TrendingItem::getVideoYoutubeId).toList());

        return storeFromTrendingItems(lists);
    }

    private List<VideoResponse> fetchTrendings(int collectSize, String regionCode) {
        List<VideoResponse> responses = new ArrayList<>();

        String pageToken = null;
        int remainCollectCount = collectSize;
        while (remainCollectCount > 0) {
            int maxResultCount = Math.min(remainCollectCount, youtubeDataApiProperties.getMaxResultCount());

            TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(maxResultCount, regionCode, pageToken);
            responses.addAll(trendingListResponse.getItems());

            if (trendingListResponse.getNextPageToken() == null) {
                break;
            }
            pageToken = trendingListResponse.getNextPageToken();

            remainCollectCount -= maxResultCount;
        }

        return responses;
    }

    private int storeFromTrendingItems(List<TrendingItem> trendingItems) {
        int storedCount = 0;

        for (TrendingItem trendingItem : trendingItems) {

            LocalDateTime dateTime = trendingItem.getDateTime();
            String videoYoutubeId = trendingItem.getVideoYoutubeId();
            int rank = trendingItem.getRank();
            String regionCode = trendingItem.getRegionCode();

            try {
                trendingCreator.create(dateTime, videoYoutubeId, rank, regionCode);
                storedCount++;
            } catch (VideoNotFoundException ex) {
                log.warn("Cannot find video despite video collection tasks. (region={}, rank={}, videoYoutubeId={})",
                        regionCode, rank, videoYoutubeId);
            }
        }

        return storedCount;
    }
}
